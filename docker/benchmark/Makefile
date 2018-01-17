.DEFAULT_GOAL=help
.PHONY: help create_table delete_table reset_table download_dataset
PACKAGE_ROOT=project.industrial
PACKAGE=$(PACKAGE_ROOT).benchmark
PORT_THRIFT_SERVER=42424
GET_KEY=4
DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/people_descriptions.csv
DATASET_FILE=$(join $(DIR), $(notdir $(DATASET_URL)))
ifeq ($(DEBUG),1)
OPTS = --debug
else
OPTS = 
endif

###############################################################
###   PLEASE APPEND YOUR RULES AT THE END OF THE MAKEFILE   ###
###############################################################


data_rate_injection_scenario: ## test the data rate injection, 80000 objects per second
	accumulo $(PACKAGE).scenarios.DataRateInjectionScenario \
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	--csv $(DATASET_FILE) \
	-u root \
	$(OPTS)
	
#data_availability_time_scenario: ## test availability of data after injection, 10s for key access, 10min for mining
#	accumulo $(PACKAGE).scenarios.DataAvailabilityTimeScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	--csv $(DATASET_FILE) \
#	--key 57562 \
#	-u root \
#	$(OPTS)
#
#data_rate_during_fullscan_scenario: ## test the data rate during a fullscan, must be 1 000 000 objects/s
#	accumulo $(PACKAGE).scenarios.DataRateDuringFullScanScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	-u root \
#	$(OPTS)
#
#time_key_access_scenario: ## test the time for getting an object by key access, must be finished before 100ms
#	accumulo $(PACKAGE).scenarios.TimeGetByKeyScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	-u root \
#	$(OPTS)
#
#time_list_keys_access_scenario: ## test the time for getting a lists of objets by keys, must be finished before 2s
#	accumulo $(PACKAGE).scenarios.TimeGetByKeysListScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	-u root \
#	$(OPTS)
#
#concurrent_actions_scenario: ## The last important scenario, lot of actions of concurrency.
#	accumulo $(PACKAGE).scenarios.ConcurrentActionsScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	-u root \
#	$(OPTS)
#
#sandbox_scenario: ## Some cool scenario
#	accumulo $(PACKAGE).scenarios.SandboxScenario \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	-u root \
#	$(OPTS)
#
#inject_data: ## Inject some data from a CSV
#	accumulo $(PACKAGE).injectors.CSVInjector \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	--csv $(DATASET_FILE) \
#	-u root \
#	$(OPTS)
#
#inject_huge_amount_data: ## Inject 60 000 000 000 objects
#	accumulo $(PACKAGE).tasks.InjectorLoopTask \
#	-i $$INSTANCE \
#	-t $$ACCUMULO_DEFAULT_TABLE \
#	-p $$ACCUMULO_PASSWORD \
#	--csv $(DATASET_FILE) \
#	-u root \
#	$(OPTS)

###############################################################
###   USEFUL RULES   ###
###############################################################


download_dataset: ## Download the dataset from github
	wget $(DATASET_URL) .

reset_table: delete_table create_table ## Delete and create the table

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
