FROM openjdk:8-jdk-alpine
MAINTAINER Mcdostone

WORKDIR /opt
USER root

RUN apk add --update --no-cache bash openssh rsync
ARG HADOOP_VERSION=2.9.0
ENV HADOOP_HOME=/opt/hadoop-${HADOOP_VERSION}
ENV PATH $PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

RUN ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key
RUN ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key
RUN ssh-keygen -qP '' -f /root/.ssh/id_rsa
RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys
ADD ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config
RUN chown root:root /root/.ssh/config
RUN ssh-keygen -A
ADD ./*.tar.gz ./
ADD ./*.xml ${HADOOP_HOME}/etc/hadoop/
RUN ${HADOOP_HOME}/bin/hdfs namenode -format

# HDFS ports
EXPOSE 50010 50020 50070 50075 50090 8020 9000
# Mapred ports
EXPOSE 10020 19888
# Yarn ports
EXPOSE 8030 8031 8032 8033 8040 8042 8088
# Other ports
EXPOSE 49707 2122

CMD /usr/sbin/sshd && ${HADOOP_HOME}/sbin/start-dfs.sh && /bin/bash
