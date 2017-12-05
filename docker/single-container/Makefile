.DEFAULT_GOAL=help
.PHONY: help cross-language sequential-batch-writer
PORT_THRIFT_SERVER=42424
TABLE_PEOPLE=people
GET_KEY=famous_people_4

###############################################################
###   PLEASE APPEND YOUR RULES AT THE END OF THE MAKEFILE   ###
##############################################################


get_all: ## Scan all the rows of given table
	accumulo project.industrial.Scan \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $$ACCUMULO_DEFAULT_TABLE


get_key: partitioning ## Returns the row which corresponds to the given rowId

get_range:  ## Returns all rows where row IDs are in the range
	@echo "Inserting some basic data: 'make partitioning'"
	@$(MAKE) partitioning > /dev/null

	accumulo project.industrial.GetKey \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $(TABLE_PEOPLE) \
	--rowId $(GET_KEY)
	@echo ""


get_key:  ## Returns the row which corresponds to the given rowId
	@echo "Inserting some basic data: 'make partitioning'"
	@$(MAKE) partitioning > /dev/null
	accumulo project.industrial.GetKey \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	-t $(TABLE_PEOPLE) \
	--rowId $(GET_KEY)
	@echo ""
	

sequential-batch-writer: ## This is a example on how write a rule
	accumulo org.apache.accumulo.examples.simple.client.SequentialBatchWriter -i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root \
	--size 50 --num 100 

start_thrift_server: ## Start the accumulo thrift server
	@sed -i "s/50096/$(PORT_THRIFT_SERVER)/" pyaccumulo/settings.py
	@sed -i "s/secret/$$ACCUMULO_PASSWORD/" pyaccumulo/settings.py	
	accumulo proxy -p $$ACCUMULO_HOME/proxy/proxy.properties & \
	sleep 5; \


partitioning: ## Write some data if a given prefix
	accumulo project.industrial.PartitioningBatchWriter \
	-i $$INSTANCE \
	-u root \
	-p $$ACCUMULO_PASSWORD \
	--table $(TABLE_PEOPLE) \
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


inject_data: ## Inject some data into accumulo
	accumulo project.industrial.PeopleInjector \
	-i $$INSTANCE \
	-t $(TABLE_PEOPLE) \
	-p $$ACCUMULO_PASSWORD \
	-u root


help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
