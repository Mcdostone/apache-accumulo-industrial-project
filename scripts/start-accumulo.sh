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


$LOG info "Start Hadoop"
$HADOOP_HOME/bin/hdfs dfs -rm -r /accumulo
$HADOOP_HOME/sbin/start-dfs.sh
echo ""

$LOG info "Start Zookeeper"
$ZOOKEEPER_HOME/bin/zkServer.sh start
echo ""

$LOG info "Start Accumulo"
$ACCUMULO_HOME/bin/start-all.sh
echo ""

$LOG info "Hadoop monitor available at http://localhost:50070/"
$LOG info "Accumulo monitor available at http://localhost:9995/"
echo ""
