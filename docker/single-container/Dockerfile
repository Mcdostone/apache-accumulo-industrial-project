FROM pi-hadoop:latest
MAINTAINER Mcdostone

WORKDIR /opt

ARG ACCUMULO_VERSION=1.8.1
ARG ACCUMULO_MEM=3GB
ARG ZOOKEEPER_VERSION=3.4.11
ENV INSTANCE=accumulo
ENV ACCUMULO_PASSWORD=root
ENV ACCUMULO_DEFAULT_TABLE=my_table
ENV ACCUMULO_HOME=/opt/accumulo-${ACCUMULO_VERSION}
ENV ZOOKEEPER_HOME=/opt/zookeeper-${ZOOKEEPER_VERSION}
ENV PATH $PATH:$ACCUMULO_HOME/bin:$ZOOKEEPER_HOME/bin
ENV ACCUMULO_MONITOR_BIND_ALL="true"

ADD ./*.tar.gz ./

RUN apk add --update --no-cache bash build-base; \
    sed -i 's?mktemp -d /tmp/accumulo-native.XXXX?mktemp -d /tmp/accumulo-native.XXXXXX?g' ${ACCUMULO_HOME}/bin/build_native_library.sh; \
    ${ACCUMULO_HOME}/bin/build_native_library.sh; \
    cp ${ACCUMULO_HOME}/conf/examples/${ACCUMULO_MEM}/standalone/* ${ACCUMULO_HOME}/conf; \
    echo -e "\nexport ACCUMULO_MONITOR_BIND_ALL=\"true\"" >> ${ACCUMULO_HOME}/conf/accumulo-env.sh; \
    cp ${ZOOKEEPER_HOME}/conf/zoo_sample.cfg ${ZOOKEEPER_HOME}/conf/zoo.cfg; \
    ln -s ${ACCUMULO_HOME}/bin/start-all.sh /usr/bin/accumulo-start-all-sh

ADD ./entrypoint.sh /usr/bin/
ADD ./reminder.sh /usr/bin/
ADD ./.bashrc /root/
RUN chmod +x /usr/bin/accumulo-start-all-sh; \
    chmod +x /usr/bin/entrypoint.sh; \
    chmod +x /usr/bin/reminder.sh

CMD [ "/usr/bin/entrypoint.sh" ]