# Benchmark!


For you benchmark, we used graphite for storing metrics, Diamond for polling system metrics of the cluster
and grafana to visualize results.

## Overview of the cluster


Machine  | VM
---       | ---
[145.239.142.186](http://145.239.142.186) | [37.59.123.111 (Metrics)](http://37.59.123.111)
[145.239.142.185](http://145.239.142.185) | [37.59.123.118 (injector)](http://37.59.123.118)
[145.239.142.188](http://145.239.142.188) | [37.59.123.138 (scanner)](http://37.59.123.138)
 | [145.239.142.187 (monitor)](http://145.239.142.187:9995) |


 <p align="center">
	<img width="90%" src="https://raw.githubusercontent.com/Mcdostone/industrial-project/master/schemas/overview_cluster.png" alt="Amazing book!"/>
</p>


## Configure your TSDB in accumulo:

Follow the documentation from accumulo to connect your TSDB with accumulo: [link](https://github.com/apache/accumulo/blob/master/assemble/conf/templates/hadoop-metrics2-accumulo.properties)


## Nginx configuration
```nginx
upstream graphite {
    server 127.0.0.1:8080 fail_timeout=0;
}

server {
    listen 80;
    access_log /var/log/nginx/graphite.access.log;
    error_log  /var/log/nginx/graphite.error.log;

    location = /favicon.ico {
        return 204;
    }

    # serve static content from the "content" directory
    location /static {
        alias /home/graphite/webapp/content;
        expires max;
    }

    location / {
        try_files $uri @graphite;
    }

    location @graphite {
 	proxy_pass_header Server;
        proxy_set_header Host $http_host;
        proxy_redirect off;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Scheme $scheme;
        proxy_connect_timeout 10;
        proxy_read_timeout 10;
        proxy_pass http://graphite; 
        add_header 'Access-Control-Allow-Origin' '37.59.123.111:3000';
        add_header 'Access-Control-Allow-Methods' 'GET, POST';
        add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type';
        add_header 'Access-Control-Allow-Credentials' 'true';   
   }
}

```
## Start services
```bash
# Start grafana
/home/grafana/bin/grafana-server start &
# Start carbon-cache (metrics collector)
/home/graphite/bin/carbon-cache.py start
# Start graphite
PYTHONPATH=/home/graphite/webapp gunicorn wsgi --workers=4 --bind=0.0.0.0:4500 --log-file=/var/log/gunicorn.log --preload --pythonpath=/home/graphite/webapp/graphite &
```