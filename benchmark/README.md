# Benchmark Accumulo !

To do our benchmarking, our industrial supervisors
adviced us to use [Grafana](https://grafana.com/), a tool for visualizing and monitoring systems.

This tool is connected to a TSDB. A TSDB collects metrics and values that are sent to the monitored system. In our case, it's accumulo.

In grafana, the TSDB is called **data source**. Some TSDB are already compatible with grafana (see [http://docs.grafana.org/features/datasources/](http://docs.grafana.org/features/datasources/)) but we need to find the one which is compatible with accumulo.
      

## Configure your TSDB in accumulo:

Follow the documentation from accumulo to connect your TSDB with accumulo: [link](https://github.com/apache/accumulo/blob/master/assemble/conf/templates/hadoop-metrics2-accumulo.properties)


## Graphite installation

For our TSDB, we choose Graphite! The following instructions explains two ways to install it:
   - installation from sources
   - docker

### From sources
  ``` bash
  apt-get install python-dev libcairo2-dev libffi-dev build-essential nginx gunicorn

  export PYTHONPATH="/opt/graphite/lib/:/opt/graphite/webapp/"
  pip install --no-binary=:all: https://github.com/graphite-project/whisper/tarball/master
  pip install --no-binary=:all: https://github.com/graphite-project/carbon/tarball/master
  pip install --no-binary=:all: https://github.com/graphite-project/graphite-web/tarball/master
  
  django-admin.py migrate --settings=graphite.settings --run-syncdb
 
  touch /var/log/nginx/graphite.access.log
  touch /var/log/nginx/graphite.error.log
  chmod 640 /var/log/nginx/graphite.*
  chown www-data:www-data /var/log/nginx/graphite.*
  ```
```nginx
# /etc/nginx/sites-available/nginx

upstream graphite {
    server 127.0.0.1:8080 fail_timeout=0;
}

server {
    listen 80 default_server;
    root /opt/graphite/webapp;

    access_log /var/log/nginx/graphite.access.log;
    error_log  /var/log/nginx/graphite.error.log;

    location = /favicon.ico {
        return 204;
    }

    # serve static content from the "content" directory
    location /static {
        alias /opt/graphite/webapp/content;
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
    }
}
```

```bash
ln -s /etc/nginx/sites-available/graphite/etc/nginx/sites-enabled
rm -f /etc/nginx/sites-enabled/default
service nginx reload
PYTHONPATH=/opt/graphite/webapp gunicorn wsgi--workers=4 --bind=127.0.0.1:8080--log-file=/var/log/gunicorn.log --preload--pythonpath=/opt/graphite/webapp/graphite &
```

### With docker
```bash
docker run -d\
 --restart=always\
 -p 80:80\
 -p 2003-2004:2003-2004\
 -p 2023-2024:2023-2024\
 -p 8125:8125/udp\
 -p 8126:8126\
 graphiteapp/graphite-statsd
```


## Test you fresh installation
  ```bash
    echo "i.hate.you.nc 1 `date +%s`" | nc -q0 localhost 2003
  ```



## Metrics we want to monitor

 - Metrics about accumulo
 - Metrics about hadoop
 - Metrics about the JVM (JMX)
 
 The metrics available with Accumulo, piped to Graphite are :
 
 - Master :
   - FilesPendingReplicationAvgTime
   - FilesPendingReplicationNumOps
   - MaxReplicationThreadsAvgTime
   - MaxReplicationThreadsNumOps
   - NumPeersAvgTime
   - NumPeersNumOps
   
  - Thrift :
    - Master :
      - ExecuteAvgTime
      - ExecuteNumOps
      - IdleAvgTime
      - IdleNumOps
   - SimpleGarbageCollector
      - ExecuteAvgTime
      - ExecuteNumOps
      - IdleAvgTime
      - IdleNumOps
    - Tabletserver
      - ExecuteAvgTime
      - ExecuteNumOps
      - IdleAvgTime
      - IdleNumOps
  - Tserver
    - MinorCompactions
      - MincAvgCount
      - MinclMaxCount
      - MinclMinCount
      - MincMaxCount
      - MincMinCount
      - MincNumOps
      - MincStdevCount
      - QueueAvgCount
      - QueuelMaxCount
      - QueuelMinCount
      - QueueMaxCount
      - QueueMinCount
      - QueueNumOps
      - QueueStdevCount  
    - Scans
      - ResultAvgCount
      - ResultlMaxCount
      - ResultlMinCount
      - ResultMaxCount
      - ResultMinCount
      - ResultNumOps
      - ResultStdevCount
      - ScanAvgCount
      - ScanlMaxCount
      - ScanlMinCount
      - ScanMaxCount
      - ScanMinCount
      - ScanNumOps
      - ScanStdevCount
    - General
      - ActiveMajCs
      - ActiveMinCs
      - Entries
      - EntriesInMem
      - FilesPerTablet
      - HoldTime
      - OnlineTablets
      - OpeningTablets
      - Queries
      - QueuedMajCs
      - QueuedMinCs
      - TotalMinCs
      - UnopenedTablets