FROM openjdk:8-jre-alpine
MAINTAINER Mcdostone

USER root

ENV HADOOP_VERSION 2.9.0
ENV HADOOP_NAME=hadoop-$HADOOP_VERSION
ENV HADOOP_HOME=/$HADOOP_NAME
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/core/$HADOOP_NAME/$HADOOP_NAME.tar.gz
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

RUN apk add --update --no-cache openssl bash openssh rsync && rm -rf /var/cache/apk/*
RUN mkdir -p "${HADOOP_HOME}"
RUN set -ex; \
    wget -O- "${HADOOP_URL}" | tar xz -C ${HADOOP_HOME} --strip-components 1
RUN rm -rf "${HADOOP_NAME}.tar.gz"
# Setup SSH
RUN ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key && \
	ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key && \
	ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa && \
	cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys
#RUN sed -i s/#PermitRootLogin.*/PermitRootLogin\ yes/ /etc/ssh/sshd_config
ADD ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config; \
	chown root:root /root/.ssh/config
RUN ssh-keygen -A
RUN /usr/sbin/sshd
# Copy conf into the image
COPY ./core-site.xml ${HADOOP_HOME}/etc/hadoop/
COPY ./hdfs-site.xml ${HADOOP_HOME}/etc/hadoop/
COPY ./mapred-site.xml ${HADOOP_HOME}/etc/hadoop/

RUN hdfs namenode -format

# Hdfs ports
EXPOSE 50010 50020 50070 50075 50090 8020 9000
# Mapred ports
EXPOSE 10020 19888
# Yarn ports
EXPOSE 8030 8031 8032 8033 8040 8042 8088
# Other ports
EXPOSE 49707 2122