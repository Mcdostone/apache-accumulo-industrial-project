.DEFAULT_GOAL=help
PACKAGE_ROOT=project.industrial
PACKAGE=$(PACKAGE_ROOT).benchmark
DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/log_access.csv
BIG_DATASET_URL=https://raw.githubusercontent.com/Mcdostone/industrial-project/master/benchmark/datasets/big_log_access.csv
DATASET_FILE=$(join $(DIR), $(notdir $(DATASET_URL)))
BIG_DATASET_FILE=$(join $(DIR), $(notdir $(BIG_DATASET_URL)))
JAVA_RUN=accumulo
ZOOKEEPERS=145.239.142.185,145.239.142.187,145.239.142.188
INSTANCE=accumulo
ACCUMULO_TABLE=ingest
ACCUMULO_PASSWORD=root
ACCUMULO_USER=root


#[maxMemory=52428800, maxLatency=120000, maxWriteThreads=3, timeout=9223372036854775807, durability=DEFAULT]'
data_rate_injection_scenario: ## test the data rate injection, 80000 objects per second
	$(JAVA_RUN) $(PACKAGE).scenarios.DataRateInjectionScenario --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--csv $(BIG_DATASET_FILE) \
	--batchThreads 10 \
	--batchLatency 120000 \
	--batchMemory $$(echo $$((500 * 1024 * 1024)))


loop_data_rate_injection_scenario: ## 20 Milliards de lignes
	$(JAVA_RUN) $(PACKAGE).scenarios.LoopDataRateInjectionScenario --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--csv $(BIG_DATASET_FILE) \
	--batchThreads 10 \
	--batchLatency 120000 \
	--batchMemory $$(echo $$((500 * 1024 * 1024)))


loop_data_rate_injection_and_check_scenario: ## 20 Milliards de lignes
	$(JAVA_RUN) $(PACKAGE).scenarios.LoopDataRateInjectionAndCheckAvailabilityScenario --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--csv $(BIG_DATASET_FILE) \
	--batchThreads 10 \
	--batchLatency 120000 \
	--batchMemory $$(echo $$((500 * 1024 * 1024)))


one_mutation: ## send 1 mutation
	$(JAVA_RUN) $(PACKAGE).scenarios.OneMutationScenario --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER)


scan_table: ## full scan on a given table
	$(JAVA_RUN) $(PACKAGE_ROOT).features.mining.GeneralScan --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \


full_scan_concurrent_scenario: ## concurrent actions, executing fullscan
	$(JAVA_RUN) $(PACKAGE).scenarios.FullScanConcurrentScenario --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER)
	

create_table: ## ask the name of the table to create
	$(JAVA_RUN) $(PACKAGE).main.CreateTable --debug \
	-i $(INSTANCE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \


delete_table: ## ask the name of the table to delete
	$(JAVA_RUN) $(PACKAGE).main.DeleteTable --debug \
	-i $(INSTANCE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	$(OPTS)


download_dataset: ## Download the dataset from github
	rm -f *.csv
	wget $(DATASET_URL)
	wget $(BIG_DATASET_URL)


sandbox: ## sandbox
	$(JAVA_RUN) $(PACKAGE).scenarios.SandboxScenario --debug \
	-i $(INSTANCE) \
	-z $(ZOOKEEPERS) \
	-t $(ACCUMULO_TABLE) \
	-u $(ACCUMULO_USER) \
	-p $(ACCUMULO_PASSWORD)


read_MR_accumulo: ## scan a table and print results
	$(JAVA_RUN) $(PACKAGE).scenarios.ReadMapReduceScenario --debug \
	-i $(INSTANCE) \
	-z $(ZOOKEEPERS) \
	-t $(ACCUMULO_TABLE) \
	-u $(ACCUMULO_USER) \
	-p $(ACCUMULO_PASSWORD) \
	--output ./ResultMR \
	--columns meta:email,meta:date,identity:firstname,identity:lastname,access:url,access:ip


write_MR_accumulo: ## write from csv loal file to accumulo table
	$(JAVA_RUN) $(PACKAGE).main.WriteHadoop --debug \
	-i $(INSTANCE) \
	-z $(ZOOKEEPERS) \
	-t $(ACCUMULO_TABLE) \
		-p $(ACCUMULO_PASSWORD) \
	--inputDir ingest


help: ## Show this help !
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'


sequential_batch_writer:
	$(JAVA_RUN) $(PACKAGE_ROOT).examples.SequentialBatchWriter --debug \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--num 1000000000 \
	--size 60


get_by_key_scenario:
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.TimeGetByKeyScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER)

get_by_key_range_scenario:
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.TimeGetByKeysListScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--maxRowId 10000000 \
	--nbKeys 45 \
	--key ejgjnz


inf_get_by_key_scenario: ## Infinite get by key
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.InfiniteGetByKeyScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--keyFile keys


inf_get_by_key_list_scenario: ## infinite get by list of keys, keys are given in a file (one row = one key)
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.InfiniteGetByKeyListScenario \
	-i $(INSTANCE) \
	-t $(ACCUMULO_TABLE) \
	-p $(ACCUMULO_PASSWORD) \
	-z $(ZOOKEEPERS) \
	-u $(ACCUMULO_USER) \
	--keyFile keys


inf_get_by_key_range_scenario: ## infinite get by range of keys
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.InfiniteGetByKeyRangeScenario --debug \
    -i $(INSTANCE) \
    -t $(ACCUMULO_TABLE) \
    -p $(ACCUMULO_PASSWORD) \
    -z $(ZOOKEEPERS) \
    -u $(ACCUMULO_USER) \
	--id 1 \
	--keyFile keys


inf_get_by_key_range_scenario_0: ## infinite get by range of keys
	$(JAVA_RUN) $(PACKAGE_ROOT).benchmark.scenarios.InfiniteGetByKeyRangeScenario --debug \
    -i $(INSTANCE) \
    -t $(ACCUMULO_TABLE) \
    -p $(ACCUMULO_PASSWORD) \
    -z $(ZOOKEEPERS) \
    -u $(ACCUMULO_USER) \
	--id 0 \
	--keyFile keys


create_keys_file: ## Create keys file
	echo "# Execute the following command"
	@echo "make scan_table > tmp1"	
	@echo  "\ntail +40 tmp1 > tmp2"
	@echo "cut -d ' ' -f1 tmp2 | uniq > keys"
	@echo "rm tmp1 tmp2"