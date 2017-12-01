FROM openjdk:8-jdk-alpine
MAINTAINER Mcdostone

WORKDIR /opt

ARG ZOOKEEPER_VERSION=3.4.11
ENV ZOOKEEPER_HOME=/opt/zookeeper-${ZOOKEEPER_VERSION}
ENV PATH $PATH:$ZOOKEEPER_HOME/bin

RUN apk add --update --no-cache bash
ADD ./* ./
RUN rm Dockerfile
RUN if [ -f /opt/zoo.cfg ]; then \
        mv /opt/zoo.cfg ${ZOOKEEPER_HOME}/conf/; else \
        cp ${ZOOKEEPER_HOME}/conf/zoo_sample.cfg ${ZOOKEEPER_HOME}/conf/zoo.cfg; \
    fi

EXPOSE 2181 2888 3888

CMD [ "zkServer.sh", "start-foreground" ]