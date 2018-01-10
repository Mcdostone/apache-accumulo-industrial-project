# Benchmark Accumulo !

To do our benchmarking, our industrial supervisors
adviced us to use [Grafana](https://grafana.com/), a tool for visualizing and monitoring systems.

This tool is connected to a TSDB. A TSDB collects metrics and values that are sent to the monitored system. In our case, it's accumulo.

In grafana, the TSDB is called **data source**. Some TSDB are already compatible with grafana (see [http://docs.grafana.org/features/datasources/](http://docs.grafana.org/features/datasources/)) but we need to find the one which is compatible with accumulo.


## Metrics we want to monitor

 - Metrics about accumulo
 - Metrics about hadoop
 - Metrics about the JVM (JMX)


## Some research about the perfect TSDB

### Timely

The NSA develops its own TSDB for accumulo. It semms to be compatible with grafana.

Last update: 5 months ago

[https://github.com/NationalSecurityAgency/timely](https://github.com/NationalSecurityAgency/timely)


### Graphite

From the documentation [https://accumulo.apache.org/1.8/accumulo_user_manual.html#_metrics](https://accumulo.apache.org/1.8/accumulo_user_manual.html#_metrics), it is possible to use graphite to store metrics from Hadoop and accumulo.


### OpenTSDB

[https://github.com/ericnewton/accumulo-opentsdb](https://github.com/ericnewton/accumulo-opentsdb)

## Configure your TSDB in accumulo:

https://github.com/apache/accumulo/blob/master/assemble/conf/templates/hadoop-metrics2-accumulo.properties