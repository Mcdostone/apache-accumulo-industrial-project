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


reset_table()
{
    $JAVA_RUN $PACKAGE.main.DeleteTable \
    -i $INSTANCE \
	-t $ACCUMULO_TABLE \
	-p $ACCUMULO_PASSWORD \
	-z $ZOOKEEPERS \
	-u $ACCUMULO_USER

    $JAVA_RUN $PACKAGE.main.CreateTable \
    -i $INSTANCE \
	-t $ACCUMULO_TABLE \
	-p $ACCUMULO_PASSWORD \
	-z $ZOOKEEPERS \
	-u $ACCUMULO_USER
}

run_with_nb_threads()
{
    echo -e "\n\n########### Injector with $1 threads"
    $JAVA_RUN $PACKAGE.scenarios.DataRateInjectionScenario \
	-i $INSTANCE \
	-t $ACCUMULO_TABLE \
	-p $ACCUMULO_PASSWORD \
	-z $ZOOKEEPERS \
	-u $ACCUMULO_USER \
    --batchThreads $1 \
	--csv $BIG_DATASET_FILE
    echo -e "########### End of the injector with $1 threads\n\n\n"
}

reset_table
run_with_nb_threads 1
threads=5
while [ $threads -le 15 ]
do
    reset_table
    run_with_nb_threads $threads
    threads=$(( $threads + 5 ))
    sleep 5
done