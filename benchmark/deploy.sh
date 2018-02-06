#!/bin/bash

deploy() {
    FILE=$1
    echo "Deploy '$(basename $1)' on VMs"

#    scp $FILE root@37.59.123.111:/root &
#    scp $FILE root@37.59.123.118:/root &
    scp $FILE root@37.59.123.138:/root &
    wait
}

# Build the jar

mvn -f ../java/pom.xml -Pprod clean compile assembly:single
echo 
# Deploy jar on VMs
deploy ../java/target/*.jar
deploy ./Makefile
deploy ./scripts
