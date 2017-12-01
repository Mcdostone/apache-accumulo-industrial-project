.DEFAULT_GOAL=help
.PHONY: build_accumulo build_zookeeper build_hadoop help download_zookeeper
BFLAGS = --network=host
ZOOKEEPER_VERSION = 3.4.11
HADOOP_VERSION = 2.9.0
ACCUMULO_VERSION = 1.8.1


download_zookeeper:
	wget -nc http://apache.crihan.fr/dist/zookeeper/zookeeper-$(ZOOKEEPER_VERSION)/zookeeper-$(ZOOKEEPER_VERSION).tar.gz -P ./zookeeper

build_zookeeper: download_zookeeper
	docker build -t pi-zookeeper:latest $(BFLAGS) zookeeper/

build_hadoop:
	wget -nc http://www.eu.apache.org/dist/hadoop/common/stable/hadoop-${HADOOP_VERSION}.tar.gz -P ./hadoop
	docker build -t pi-hadoop:latest $(BFLAGS) hadoop/

build_accumulo: build_hadoop download_zookeeper
	wget -nc http://apache.crihan.fr/dist/accumulo/$(ACCUMULO_VERSION)/accumulo-$(ACCUMULO_VERSION)-bin.tar.gz -P ./accumulo
	cp ./zookeeper/zookeeper-$(ZOOKEEPER_VERSION).tar.gz ./accumulo/
	docker build -t pi-accumulo:latest $(BFLAGS) accumulo/

build_all: build_zookeeper build_hadoop build_accumulo

up:
	docker-compose up -d

delete_all_images:
	docker rmi -f $$(docker images -aq)

clean:
	rm -f accumulo/*.tar.gz zookeeper/*.tar.gz hadoop/*.tar.gz

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
	#						 Docker version 17.11.0-ce, build 1caf76c
	#                    docker-compose version 1.17.1, build 6d101fb
