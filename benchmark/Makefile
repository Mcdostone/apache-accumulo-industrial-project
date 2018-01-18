.DEFAULT_GOAL=help
PACKAGE_ROOT=project.industrial
PACKAGE=$(PACKAGE_ROOT).benchmark
DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/log_access.csv
BIG_DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/big_log_access.csv

DATASET_FILE=$(join $(DIR), $(notdir $(DATASET_URL)))
ifeq ($(DEBUG),1)
OPTS = --debug
else
OPTS = 
endif
JAVA_RUN=java -cp /root/sandbox-accumulo-1.0-jar-with-dependencies.jar 
ZOOKEEPERS=145.239.142.185,145.239.142.187,145.239.142.188
INSTANCE=accumulo
ACCUMULO_TABLE=test
ACCUMULO_PASSWORD=root
ACCUMULO_USER=root


data_rate_injection_scenario: ## test the data rate injection, 80000 objects per second
	$(JAVA_RUN) $(PACKAGE).scenarios.DataRateInjectionScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--csv $(DATASET_FILE) \
	$(OPTS)


test_graphite: ## test communication between java code and graphite
	$(JAVA_RUN) $(PACKAGE).core.MetricsManager 


download_dataset: ## Download the dataset from github
	rm -f *.csv
	wget $(DATASET_URL)
	wget $(BIG_DATASET_URL)
	

help: ## Show this help !
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

