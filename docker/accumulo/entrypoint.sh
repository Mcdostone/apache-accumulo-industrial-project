#!/bin/bash

log()
{
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

/usr/sbin/sshd
${HADOOP_HOME}/sbin/start-dfs.sh
log INFO "starting HDFS"
sed -i "s/localhost:2181/$ZOOKEEPER_PORT_2181_TCP_ADDR:2181/g" ${ACCUMULO_HOME}/conf/accumulo-site.xml
sed -i "s#<value></value>#<value>hdfs://localhost:9000/accumulo</value>#g" ${ACCUMULO_HOME}/conf/accumulo-site.xml
sed -i "s/DEFAULT/$(tr -cd '[:alnum:]' < /dev/urandom | fold -w30 | head -n1)/g" ${ACCUMULO_HOME}/conf/accumulo-site.xml
log INFO "HDFS is running !"

log INFO "Zookeeper running on $ZOOKEEPER_PORT_2181_TCP_ADDR"
printf "\e[1m\e[33m%s\e[39m\e[21m %s\n" "You have to run manually accumulo !"
echo -e "\taccumulo init"
echo -e "\t\$ACCUMULO_HOME/bin/start-all.sh\n"

log info "Hadoop monitor available at http://localhost:50070/"
log info "Accumulo monitor will be available at http://localhost:9995/"

# Accumulo and hadoop separated
# sed -i "s#<value></value>#<value>hdfs://$HADOOP_PORT_9000_TCP_ADDR:9000/accumulo</value>#g" ${ACCUMULO_HOME}/conf/accumulo-site.xml


# modele ouvert: la porte est ouvert

# La porte d'un modele est
# Mery taff avec des modeles fermés. les variables sont modifiés par des events précis