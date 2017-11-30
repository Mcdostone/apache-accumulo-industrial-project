.DEFAULT_GOAL=help
.PHONY: build_accumulo build_zookeeper build_hadoop help
BFLAGS = --network=host
ZOOKEEPER_VERSION = 3.4.11
HADOOP_VERSION = 2.9.0
ACCUMULO_VERSION = 1.8.1


build_zookeeper: 	
	wget -nc http://apache.crihan.fr/dist/zookeeper/zookeeper-$(ZOOKEEPER_VERSION)/zookeeper-$(ZOOKEEPER_VERSION).tar.gz -P ./zookeeper
	docker build -t zookeeper-$(ZOOKEEPER_VERSION) $(BFLAGS) zookeeper/

build_hadoop:
	wget -nc http://www.eu.apache.org/dist/hadoop/common/stable/hadoop-${HADOOP_VERSION}.tar.gz -P ./hadoop
	docker build -t hadoop-$(HADOOP_VERSION) $(BFLAGS) hadoop/

build_accumulo:
	wget -nc http://apache.crihan.fr/dist/accumulo/$(ACCUMULO_VERSION)/accumulo-$(ACCUMULO_VERSION)-bin.tar.gz -P ./accumulo
	docker build -t accumulo-$(ACCUMULO_VERSION) $(BFLAGS) accumulo/

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