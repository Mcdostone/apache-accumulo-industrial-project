#!/bin/bash

INSTANCE=accumulo
ZOOKEEPERS=145.239.142.185,145.239.142.187,145.239.142.188
ACCUMULO_TABLE=test
ACCUMULO_PASSWORD=root
ACCUMULO_USER=root
PACKAGE_ROOT=project.industrial
PACKAGE=$PACKAGE_ROOT.benchmark
BIG_DATASET_FILE=/root/big_log_access.csv
JAVA_RUN="~/accumulo/bin/accumulo jar /root/sandbox-accumulo-1.0-jar-with-dependencies.jar"


run()
{
    $JAVA_RUN $PACKAGE.scenarios.DataRateInjectionRandomDataScenario \
	-i $INSTANCE \
	-t $ACCUMULO_TABLE \
	-p $ACCUMULO_PASSWORD \
	-z $ZOOKEEPERS \
	-u $ACCUMULO_USER \
    --batchThreads 10 \
	--batchLatency 1000 \
	--batchMemory 1000000
}

run