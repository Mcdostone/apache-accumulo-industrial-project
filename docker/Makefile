.DEFAULT_GOAL=help
.PHONY: build_java transfer_jar transfer_dataset run clean help
ACCUMULO_VERSION = 1.8.1


build_java: ## Generate the jar for features
	mvn -f ../java/pom.xml -Pprod clean compile assembly:single


transfer_jar:  ## Transfer the generated jar into the currently running container
	@CONTAINER_ID=$$(docker ps -aqf "name=pi-accumulo"); \
	if [ -z "$$CONTAINER_ID" ]; then \
		echo "There is currently no accumulo container running"; \
	else \
	 	$(MAKE) build_java; \
		docker cp $$(pwd)/../java/target/sandbox-accumulo-1.0-jar-with-dependencies.jar $$CONTAINER_ID:/opt/accumulo-$(ACCUMULO_VERSION)/lib/ext/; \
	fi


transfer_dataset:  ## Transfer the dataset to the current running container
	@CONTAINER_ID=$$(docker ps -aqf "name=pi-accumulo"); \
	if [ -z "$$CONTAINER_ID" ]; then \
		echo "There is currently no accumulo container running"; \
	else \
		docker cp $$(pwd)/../benchmark/datasets/*.csv $$CONTAINER_ID:/opt/benchmark; \
	fi


run: # Run the container with accumulo
	docker run \
		--rm \
		-it \
		-p 50070:50070 \
		-p 9995:9995 \
		--name pi-accumulo \
		-v $$(pwd)/features/Makefile:/opt/features/Makefile:rw \
		-v $$(pwd)/benchmark/Makefile:/opt/benchmark/Makefile:rw \
		mcdostone/accumulo bash


clean: ## Remove all archives files and docker images
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
