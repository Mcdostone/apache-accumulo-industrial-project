/usr/sbin/sshd
${HADOOP_HOME}/sbin/start-dfs.sh
sed -i "s/localhost:2181/$ZOOKEEPER_PORT_2181_TCP_ADDR:2181/g" ${ACCUMULO_HOME}/conf/accumulo-site.xml
sed -i "s#<value></value>#<value>hdfs://localhost:9000/accumulo</value>#g" ${ACCUMULO_HOME}/conf/accumulo-site.xml
sed -i "s/DEFAULT/$(tr -cd '[:alnum:]' < /dev/urandom | fold -w30 | head -n1)/g" ${ACCUMULO_HOME}/conf/accumulo-site.xml

${ACCUMULO_HOME}/bin/start-all.sh
# Accumulo and hadoop separated
# sed -i "s#<value></value>#<value>hdfs://$HADOOP_PORT_9000_TCP_ADDR:9000/accumulo</value>#g" ${ACCUMULO_HOME}/conf/accumulo-site.xml


# modele ouvert: la porte est ouvert

# La porte d'un modele est
# Mery taff avec des modeles fermés. les variables sont modifiés par des events précis