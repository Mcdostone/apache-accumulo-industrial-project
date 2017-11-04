#!/bin/bash

LOG=$(cd -P "$( dirname $0 )" && pwd)/log.sh

$LOG info "Init HDFS nameNode"
$HADOOP_HOME/bin/hdfs namenode -format
echo ""