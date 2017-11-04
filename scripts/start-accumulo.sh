#!/bin/bash

INSTALL_DIR=~/installs
ACCUMULO=$INSTALL_DIR/accumulo
LOG=$(cd -P "$( dirname $0 )" && pwd)/log.sh


$LOG info "Start Hadoop"
$HADOOP_HOME/sbin/start-dfs.sh
echo ""

$LOG info "Start Zookeeper"
$ZOOKEEPER_HOME/bin/zkServer.sh start
echo ""

$ACCUMULO/bin/accumulo init
if [ $? -eq 255 ]; then $LOG warn "Accumulo is already initialized"; else $LOG info "Init accumulo"; fi
echo ""

$LOG info "Start Accumulo"
$ACCUMULO/bin/start-all.sh
echo ""

$LOG info "Hadoop monitor available at http://localhost:50070/"
$LOG info "Accumulo monitor available at http://localhost:9995/"
echo ""