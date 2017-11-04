#!/bin/bash

INSTALL_DIR=~/installs
ACCUMULO=$INSTALL_DIR/accumulo
LOG=$(cd -P "$( dirname $0 )" && pwd)/log.sh


$LOG info "Stop Accumulo"
$ACCUMULO/bin/stop-all.sh
if [ $? -ne 0 ]; then $LOG warn "Accumulo is problably already stopped"; fi
echo ""

$LOG info "Stop Zookeeper"
$ZOOKEEPER_HOME/bin/zkServer.sh stop
if [ $? -ne 0 ]; then $LOG warn "Zookeeper is problably already stopped"; fi
echo ""


$LOG info "Stop Hadoop"
$HADOOP_HOME/sbin/stop-dfs.sh
if [ $? -ne 0 ]; then $LOG warn "Hadoop is problably already stopped"; fi
echo ""
