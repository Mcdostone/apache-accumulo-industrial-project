.DEFAULT_GOAL=help
.PHONY: help cross-language sequential-batch-writer
PACKAGE_ROOT=project.industrial
PACKAGE=$(PACKAGE_ROOT).benchmark
PORT_THRIFT_SERVER=42424
GET_KEY=4
DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/people_descriptions.csv
DATASET_FILE=$(join $(DIR), $(notdir $(DATASET_URL)))

###############################################################
###   PLEASE APPEND YOUR RULES AT THE END OF THE MAKEFILE   ###
###############################################################


inject_data: ## Inject some data into accumulo
	accumulo $(PACKAGE).tasks.InjectorTask \
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	--csv $(DATASET_FILE) \
	-u root
	

###############################################################
###   USEFUL RULES   ###
###############################################################


download_dataset: ## Download the dataset from github
	wget $(DATASET_URL) .


delete_table: ## Delete the default table
	accumulo shell -u root -p $$ACCUMULO_PASSWORD -e "deletetable $$ACCUMULO_DEFAULT_TABLE"


create_table: ## Create the default table
	accumulo shell -u root -p $$ACCUMULO_PASSWORD -e "createtable $$ACCUMULO_DEFAULT_TABLE"


list_classes: ## show all java classes available
	@if [ ! -z "$(ls -A $$ACCUMULO_HOME/lib/ext)" ]; then \
        printf "\e[31;1m %-40s \e[0m\n" "There is no jar loaded! (make transfer_jar) "; \
    else \
		echo -e "accumulo $(PACKAGE_ROOT).Help\n"; \
		accumulo $(PACKAGE_ROOT).Help; \
	fi


help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
