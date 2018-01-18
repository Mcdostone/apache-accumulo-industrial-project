# Benchmark!


For you benchmark, we used graphite for storing metrics, Diamond for polling system metrics of the cluster
and grafana to visualize results.

## Overview of the cluster


Machine  | VM
---       | ---
[145.239.142.186](145.239.142.186) | [37.59.123.111 (Metrics)](37.59.123.111)
[145.239.142.185](145.239.142.185) | [37.59.123.118 (injector)](37.59.123.118)
[145.239.142.188](145.239.142.188) | [37.59.123.138 (scanner)](37.59.123.138)
 | [145.239.142.187](145.239.142.187) |


 <p align="center">
	<img width="90%" src="https://raw.githubusercontent.com/Mcdostone/industrial-project/master/schemas/overview_cluster.png" alt="Amazing book!"/>
</p>


## Configure your TSDB in accumulo:

Follow the documentation from accumulo to connect your TSDB with accumulo: [link](https://github.com/apache/accumulo/blob/master/assemble/conf/templates/hadoop-metrics2-accumulo.properties)





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