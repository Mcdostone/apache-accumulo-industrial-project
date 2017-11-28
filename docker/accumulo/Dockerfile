FROM openjdk:8-jdk-alpine
MAINTAINER Mcdostone

WORKDIR /opt

RUN apk add --update --no-cache bash build-base
ARG ACCUMULO_VERSION=1.8.1
ARG ACCUMULO_MEM=3GB
ENV ACCUMULO_HOME=/opt/accumulo-${ACCUMULO_VERSION}
ENV ACCUMULO_ARCHIVE=accumulo-${ACCUMULO_VERSION}-bin.tar.gz

ADD ./${ACCUMULO_ARCHIVE} ./
# Fix for mktemp
RUN sed -i 's?mktemp -d /tmp/accumulo-native.XXXX?mktemp -d /tmp/accumulo-native.XXXXXX?g' ${ACCUMULO_HOME}/bin/build_native_library.sh
RUN ${ACCUMULO_HOME}/bin/build_native_library.sh
# RUN ${ACCUMULO_HOME}/bin/bootstrap_config.sh
RUN cp ${ACCUMULO_HOME}/conf/examples/${ACCUMULO_MEM}/standalone/* ${ACCUMULO_HOME}/conf