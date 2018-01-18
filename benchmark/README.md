# Benchmark!


For you benchmark, we used graphite for storing metrics, Diamond for polling system metrics of the cluster
and grafana to visualize results.

## Overview of the cluster
3 VMs
 - 


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