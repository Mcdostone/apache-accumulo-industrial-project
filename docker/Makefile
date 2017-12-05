.DEFAULT_GOAL=help
.PHONY: build_hadoop help download_zookeeper download_accumulo build run run_java clean build_java
FLAGS = --network=host
ZOOKEEPER_VERSION = 3.4.11
HADOOP_VERSION = 2.9.0
ACCUMULO_VERSION = 1.8.1
ACCUMULO_HOME=/opt/accumulo-$(ACCUMULO_VERSION)

download_zookeeper: ## Download zookeeper in zookeeper/ folder
	-wget -nc http://apache.crihan.fr/dist/zookeeper/zookeeper-$(ZOOKEEPER_VERSION)/zookeeper-$(ZOOKEEPER_VERSION).tar.gz -P ./zookeeper

download_accumulo: ## Download accumulor in accumulo/ folder
	-wget -nc http://apache.crihan.fr/dist/accumulo/$(ACCUMULO_VERSION)/accumulo-$(ACCUMULO_VERSION)-bin.tar.gz  -O ./accumulo/accumulo-$(ACCUMULO_VERSION).tar.gz

build_hadoop: ## Build the hadoop docker image
	wget -nc http://www.eu.apache.org/dist/hadoop/common/stable/hadoop-${HADOOP_VERSION}.tar.gz -P ./hadoop
	docker build $(FLAGS) \
		-t \pi-hadoop:latest \
		--build-arg HADOOP_VERSION=$(HADOOP_VERSION) \
		 hadoop/

build: build_hadoop download_accumulo download_zookeeper ## Build the single-container accumulo docker image
	cp ./zookeeper/zookeeper-$(ZOOKEEPER_VERSION).tar.gz ./single-container/
	cp ./accumulo/accumulo-$(ACCUMULO_VERSION).tar.gz ./single-container/
	docker build $(FLAGS) \
		-t pi-accumulo:latest \
		--build-arg ACCUMULO_VERSION=$(ACCUMULO_VERSION) \
		--build-arg ZOOKEEPER_VERSION=$(ZOOKEEPER_VERSION) \
		single-container

build_java: ## Generate the jar for features
	mvn -f ../java/pom.xml -Pprod clean compile assembly:single

transfer_jar:  ## Transfer the generated jar into the currently running container
	@CONTAINER_ID=$$(docker ps -aqf "name=pi-accumulo"); \
	if [ -z "$$CONTAINER_ID" ]; then \
		echo "There is currently no accumulo container running"; \
	else \
	 	$(MAKE) build_java; \
		docker cp $$(pwd)/../java/target/features-accumulo-1.0-jar-with-dependencies.jar $$CONTAINER_ID:/opt/accumulo-$(ACCUMULO_VERSION)/lib/ext/; \
	fi	

run: # Run the container with accumulo
	docker run \
		--rm \
		-it \
		-p 50070:50070 \
		-p 9995:9995 \
		--name pi-accumulo \
		-v $$(pwd)/single-container/Makefile:/opt/Makefile:rw \
		pi-accumulo:latest bash
		#-v $$(pwd)/../java/target/:$(ACCUMULO_HOME)/lib/ext/:rw

clean: ## Remove all archives files and docker images
	rm -f accumulo/*.tar.gz zookeeper/*.tar.gz hadoop/*.tar.gz
	docker rmi -f $$(docker images -aq)

help: ## Show this help
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
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
	@echo ''
