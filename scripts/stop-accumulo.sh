#!/bin/bash

LOG=$(cd -P "$( dirname $0 )" && pwd)/log.sh

check_environnment_variables()
{
    for VAR in "$@"; do
        if [ -z "$(printenv | grep $VAR)" ]; then
           $LOG fail "$VAR is not configured, please configure this environnment variable to continue the installation"
           exit 1
        else
           $LOG info "$VAR is configured"
        fi
    done
}

check_environnment_variables JAVA_HOME ZOOKEEPER_HOME HADOOP_HOME ACCUMULO_HOME

$LOG info "Stop Accumulo"
$ACCUMULO_HOME/bin/stop-all.sh
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
