FROM openjdk:8-jre-alpine
MAINTAINER Mcdostone

ENV HADOOP_VERSION 2.9.0
ENV HADOOP_NAME=hadoop-$HADOOP_VERSION
ENV HADOOP_HOME=/$HADOOP_NAME
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/core/$HADOOP_NAME/$HADOOP_NAME.tar.gz

RUN apk add --update openssl
RUN mkdir -p "${HADOOP_HOME}"; \
    set -ex; \    
    wget -qO- "${HADOOP_URL}" | tar xz -C ${HADOOP_HOME} --strip-components 1; \
    rm -rf "${HADOOP_NAME}.tar.gz"

ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# HDFS
EXPOSE 8020 14000 50070 50470

# MapReduce
EXPOSE 10020 13562	19888
