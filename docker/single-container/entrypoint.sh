#!/bin/bash

log() {
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters, expected 2 parameters, given $#"
        echo "Param 1: Level of log (info|warning|error)"
        echo "Param 2: Message to log"
        exit 1
    fi
    LEVEL=$(echo $1 | tr "[:lower:]" "[:upper:]")
    shift
    if [ "$LEVEL" = "INFO" ] ; then
        COLOR="\e[34m"
    fi
    if [ "$LEVEL" = "WARN" ] ; then
        COLOR="\e[33m"
    fi
    if [ "$LEVEL" = "FAIL" ] ; then
        COLOR="\e[31m"
    fi
    if [ -n "$COLOR"  ] ; then
        printf "\e[1m[${COLOR}%s\e[39m]\e[21m %s\n" "$LEVEL" "$*"
    fi
}

setup_accumulo_configuration() {
    # The adress of HDFS master
    sed -i "s#<value></value>#<value>hdfs://localhost:9000/accumulo</value>#" ${ACCUMULO_HOME}/conf/accumulo-site.xml
    # Generate random secret token
    sed -i "s/DEFAULT/$(tr -cd '[:alnum:]' < /dev/urandom | fold -w30 | head -n1)/" ${ACCUMULO_HOME}/conf/accumulo-site.xml
    # Modify password of user root
    sed -i "s#<value>secret</value>#<value>$ACCUMULO_PASSWORD</value>#" ${ACCUMULO_HOME}/conf/accumulo-site.xml 
}

start_zookeeper() {
    log INFO "starting zookeeper"
    zkServer.sh start
}

start_sshd() {
    log INFO s"tarting sshd"
    /usr/sbin/sshd
}

start_hdfs() {
    log INFO "starting HDFS"
    ${HADOOP_HOME}/sbin/start-dfs.sh
    log INFO "HDFS is running !"
}

start_accumulo() {
    log INFO "Initialize accumulo"
    accumulo init --instance-name $INSTANCE --password "$ACCUMULO_PASSWORD" --clear-instance-name
    if [ $? -ne 0 ]; then
        log warn "Cannot initialize accumulo"
        exit 1
    fi
    log info "Starting accumulo"
    accumulo-start-all-sh
}

print_info() {
    log info "Hadoop monitor available at http://localhost:50070/"
    log info "Accumulo monitor will be available at http://localhost:9995/"

}

start_all() {
    setup_accumulo_configuration
    start_sshd
    start_zookeeper
    start_hdfs
    start_accumulo
    print_info
}

start_all