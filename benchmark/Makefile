.DEFAULT_GOAL=help
PACKAGE_ROOT=project.industrial
PACKAGE=$(PACKAGE_ROOT).benchmark
DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/log_access.csv
BIG_DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/big_log_access.csv
DATASET_FILE=$(join $(DIR), $(notdir $(DATASET_URL)))
BIG_DATASET_FILE=$(join $(DIR), $(notdir $(BIG_DATASET_URL)))
JAVA_RUN=java -cp /root/sandbox-accumulo-1.0-jar-with-dependencies.jar -Xms15G
ZOOKEEPERS=145.239.142.185,145.239.142.187,145.239.142.188
INSTANCE=accumulo
ACCUMULO_TABLE=test
ACCUMULO_PASSWORD=root
ACCUMULO_USER=root

#--batchThreads 15 
#--batchMemory $$(echo $$((250 * 1024 * 1024))) 
data_rate_injection_scenario: ## test the data rate injection, 80000 objects per second
	$(JAVA_RUN) $(PACKAGE).scenarios.DataRateInjectionScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--csv $(BIG_DATASET_FILE) \
	# --batchLatency 5000
	# --batchMemory $$(echo $$((500 * 1024 * 1024)))
	# --batchThreads 5


scan_table: ## full scan on a given table
	$(JAVA_RUN) $(PACKAGE_ROOT).features.mining.GeneralScan \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \


test_graphite: ## test communication between java code and graphite
	$(JAVA_RUN) $(PACKAGE).core.MetricsManager 


create_table: ## test the data rate injection, 80000 objects per second
	$(JAVA_RUN) $(PACKAGE).main.CreateTable \
	-i $(INSTANCE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \


check_metadata_problems: ## 
	$(JAVA_RUN) org.apache.accumulo.server.util.CheckForMetadataProblems \
	-i $(INSTANCE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \


download_dataset: ## Download the dataset from github
	rm -f *.csv
	wget $(DATASET_URL)
	wget $(BIG_DATASET_URL)
	
read_MR_accumulo: ## scan a table and print results
	$(JAVA_RUN) $(PACKAGE).main.MapReduce \
	-i $(INSTANCE) \
	-z $(ZOOKEEPERS) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	--output ./resultMR \
	--columns colfam1:colqual1 \

write_MR_accumulo: ## write from csv loal file to accumulo table
	$(JAVA_RUN) $(PACKAGE).main.WriteHadoop \
	-i $(INSTANCE) \
	-z $(ZOOKEEPERS) \
	-t samia \
	-p $(ACCUMULO_PASSWORD) \
	--inputDir datatest \
	--workDir ~/work

sandbox: ## Test some stuff
	$(JAVA_RUN) $(PACKAGE).scenarios.SandboxScenario \
	-i $(INSTANCE) \
	-p $(ACCUMULO_PASSWORD) \
	-t $(ACCUMULO_TABLE) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--batchThreads 15 \
	--batchMemory $$(echo $$((250 * 1024 * 1024))) \
	--csv $(BIG_DATASET_FILE) \


help: ## Show this help !
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'


sequential_batch_writer:
	$(JAVA_RUN) $(PACKAGE_ROOT).examples.SequentialBatchWriter \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--num 1000000000 \
	--size 60
	

