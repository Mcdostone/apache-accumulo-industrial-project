.DEFAULT_GOAL=help
.PHONY: build_hadoop help download_zookeeper download_accumulo build run run_java clean build_java
FLAGS = --network=host
ZOOKEEPER_VERSION = 3.4.11
HADOOP_VERSION = 2.9.0
ACCUMULO_VERSION = 1.8.1
ACCUMULO_HOME=/opt/accumulo-$(ACCUMULO_VERSION)

download_zookeeper:
	-wget -nc http://apache.crihan.fr/dist/zookeeper/zookeeper-$(ZOOKEEPER_VERSION)/zookeeper-$(ZOOKEEPER_VERSION).tar.gz -P ./zookeeper

download_accumulo:
	-wget -nc http://apache.crihan.fr/dist/accumulo/$(ACCUMULO_VERSION)/accumulo-$(ACCUMULO_VERSION)-bin.tar.gz  -O ./accumulo/accumulo-$(ACCUMULO_VERSION).tar.gz

build_hadoop:
	wget -nc http://www.eu.apache.org/dist/hadoop/common/stable/hadoop-${HADOOP_VERSION}.tar.gz -P ./hadoop
	docker build $(FLAGS) \
		-t \pi-hadoop:latest \
		--build-arg HADOOP_VERSION=$(HADOOP_VERSION) \
		 hadoop/

build: build_hadoop download_accumulo download_zookeeper
	cp ./zookeeper/zookeeper-$(ZOOKEEPER_VERSION).tar.gz ./single-container/
	cp ./accumulo/accumulo-$(ACCUMULO_VERSION).tar.gz ./single-container/
	docker build $(FLAGS) \
		-t pi-accumulo:latest \
		--build-arg ACCUMULO_VERSION=$(ACCUMULO_VERSION) \
		--build-arg ZOOKEEPER_VERSION=$(ZOOKEEPER_VERSION) \
		single-container

build_java:
	mvn package -f ../java/pom.xml

run_java:	
	docker run \
		--rm \
		-it \
		-p 50070:50070 \
		-p 9995:9995 \
		-v $$(pwd)/../java/target/:$(ACCUMULO_HOME)/lib/ext:rw \
		pi-accumulo:latest bash

run:
	docker run \
	--rm \
	-it \
	-p 50070:50070 \
	-p 9995:9995 \
	pi-accumulo:latest bash	

clean:
	rm -f accumulo/*.tar.gz zookeeper/*.tar.gz hadoop/*.tar.gz
	docker rmi -f $$(docker images -aq)

help:
	#                    __                 ___      ___             
	#  /'\_/`\          /\ \              /'___\ __ /\_ \            
	# /\      \     __  \ \ \/'\      __ /\ \__//\_\\//\ \      __   
	# \ \ \__\ \  /'__`\ \ \ , <    /'__`\ \ ,__\/\ \ \ \ \   /'__`\ 
	#  \ \ \_/\ \/\ \L\.\_\ \ \\`\ /\  __/\ \ \_/\ \ \ \_\ \_/\  __/ 
	#   \ \_\\ \_\ \__/.\_\\ \_\ \_\ \____\\ \_\  \ \_\/\____\ \____\
	#    \/_/ \/_/\/__/\/_/ \/_/\/_/\/____/ \/_/   \/_/\/____/\/____/
	#
	#                                        Version 0.1 by mcdostone
	#                                          Tested on Ubuntu 17.10
	#                        Docker version 17.11.0-ce, build 1caf76c
	#                    docker-compose version 1.17.1, build 6d101fb
	#
	# Main rules:
	#	- build		Build the docker image for accumulo (single container)
	#	- run		Run the accumulo image
	#	- clean		Delete tar.gz archives and all docker images
	#	- build_hadoop	Build the image for hadoop
	#
	@echo ''
