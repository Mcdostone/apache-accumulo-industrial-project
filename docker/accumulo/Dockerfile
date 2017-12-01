FROM pi-hadoop:latest
MAINTAINER Mcdostone

WORKDIR /opt

ARG ACCUMULO_VERSION=1.8.1
ARG ACCUMULO_MEM=3GB
ARG ZOOKEEPER_HOST=zookeeper
ARG HADOOP_HOST=hadoop
ENV ACCUMULO_HOME=/opt/accumulo-${ACCUMULO_VERSION}
ENV ACCUMULO_MONITOR_BIND_ALL="true"
ENV ZOOKEEPER_HOME /usr/lib/zookeeper
ENV PATH $PATH:$ACCUMULO_HOME/bin
ARG ZOOKEEPER_VERSION=3.4.11
ENV ZOOKEEPER_HOME=/opt/zookeeper-${ZOOKEEPER_VERSION}
ENV ZOOKEEPER_ARCHIVE=zookeeper-${ZOOKEEPER_VERSION}.tar.gz
ENV PATH $PATH:$ZOOKEEPER_HOME/bin

ADD ./*.tar.gz ./
ADD ./entrypoint.sh /usr/bin/

RUN apk add --update --no-cache bash build-base; \
    sed -i 's?mktemp -d /tmp/accumulo-native.XXXX?mktemp -d /tmp/accumulo-native.XXXXXX?g' ${ACCUMULO_HOME}/bin/build_native_library.sh; \
    ${ACCUMULO_HOME}/bin/build_native_library.sh; \
    cp ${ACCUMULO_HOME}/conf/examples/${ACCUMULO_MEM}/standalone/* ${ACCUMULO_HOME}/conf; \
    echo -e "\nexport ACCUMULO_MONITOR_BIND_ALL=\"true\"" >> ${ACCUMULO_HOME}/conf/accumulo-env.sh; \
    chmod +x /usr/bin/entrypoint.sh

EXPOSE 2181 2888 3888
CMD [ "bash", "/usr/bin/entrypoint.sh" ]