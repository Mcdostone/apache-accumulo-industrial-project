.DEFAULT_GOAL=help
.PHONY: help cross-language sequential-batch-writer
PACKAGE=project.industrial
PORT_THRIFT_SERVER=42424
GET_KEY=4


###############################################################
###   PLEASE APPEND YOUR RULES AT THE END OF THE MAKEFILE   ###
###############################################################


get_all: ## Scan all the rows of given table
	accumulo $(PACKAGE).mining.GeneralScan \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE


get_range:  ## Returns all rows where row IDs are in the range
	accumulo $(PACKAGE).mining.GetByRange \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE \
	--startAt 101 \
	--endAt 253


get_list:  ## Returns the row where row IDs are in the list
	accumulo $(PACKAGE).mining.GetByList \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE \
	--id $(GET_KEY) \
	--id 89 \
	--id 52 \
	--id 426 \
	--id 3214 \


get_key:  ## Returns the row which corresponds to the given rowId
	accumulo $(PACKAGE).GetByKey \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE \
	--id $(GET_KEY)
	
delete_row: ## Deletes rows matching range, column family and colum qualifier
	accumulo $(PACKAGE).GeneralDelete \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE \
	--ranges -row_10,row_20-row_30,row_70-
	--colfam colfam
	--colqual colqual_2
	
get_regex: ## Returns row matching the given regex
	accumulo $(PACKAGE).mining.GetByRegex \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE \
	--row .*4.*
	--colfam col.*
	--colqual .*1.*
	--value .*3.*

sequential-batch-writer: ## This is a example on how write a rule
	accumulo org.apache.accumulo.examples.simple.client.SequentialBatchWriter\
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root \
	--size 50 --num 100 

set_ttl: ## Set a time-to-live for a given table. After that, all rows will be deleted
	accumulo $(PACKAGE).SetTTL \
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root \
	-ttl 30000

start_thrift_server: ## Start the accumulo thrift server
	@sed -i "s/50096/$(PORT_THRIFT_SERVER)/" pyaccumulo/settings.py
	@sed -i "s/secret/$$ACCUMULO_PASSWORD/" pyaccumulo/settings.py	
	accumulo proxy -p $$ACCUMULO_HOME/proxy/proxy.properties & \
	sleep 5; \


partitioning: ## Write some data if a given prefix
	accumulo $(PACKAGE).PartitioningBatchWriter \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	--table $$ACCUMULO_DEFAULT_TABLE \
	--prefix "famous_people"


cross-language: start_thrift_server ## Optional feature: Other programming languages to interact with accumulo
	@if [ ! -d pyaccumulo ]; then \
		git clone https://github.com/jt6211/pyaccumulo.git; \
		pip install -r pyaccumulo/requirements.txt; \
	fi
	@if ! nc -z localhost 42424; then \
		accumulo proxy -p $$ACCUMULO_HOME/proxy/proxy.properties & \
		sleep 5; \
	fi
	@cd pyaccumulo; \
	export PYTHONPATH="."; \
	echo "Create table 'analytics' (see examples/analytics.py)"; \
	echo "Execute 'python examples/analytics.py'"; \
	python examples/analytics.py

addTwice: ## Add two identical rows
	accumulo $(PACKAGE).AddTwice \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	--table $$ACCUMULO_DEFAULT_TABLE


inject_column: ## Inject the column 'gender' into the table
	accumulo $(PACKAGE).injectors.AddColumn \
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root

updateRow: ## Update a row
	accumulo $(PACKAGE).Update \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	--table $$ACCUMULO_DEFAULT_TABLE


###############################################################
###   USEFUL RULES   ###
###############################################################

inject_data: ## Inject some data into accumulo
	accumulo $(PACKAGE).injectors.PeopleInjector \
	-i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root


delete_table: ## Delete the default table
	accumulo shell -u root -p $$ACCUMULO_PASSWORD -e "deletetable $$ACCUMULO_DEFAULT_TABLE"


help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
