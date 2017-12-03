.DEFAULT_GOAL=help
.PHONY: help cross-language sequential-batch-writer
PORT_THRIFT_SERVER=42424

help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

sequential-batch-writer: ## This is a example on how write a rule
	accumulo org.apache.accumulo.examples.simple.client.SequentialBatchWriter -i $$INSTANCE \
	-t $$ACCUMULO_DEFAULT_TABLE \
	-p $$ACCUMULO_PASSWORD \
	-u root \
	--size 50 --num 100 

cross-language: ## Optional feature: Other programming languages to interact with accumulo
	@if [ ! -d pyaccumulo ]; then \
		git clone https://github.com/jt6211/pyaccumulo.git; \
		pip install -r pyaccumulo/requirements.txt; \
	fi
	@sed -i "s/50096/$(PORT_THRIFT_SERVER)/" pyaccumulo/settings.py
	@sed -i "s/secret/$$ACCUMULO_PASSWORD/" pyaccumulo/settings.py
	@if ! nc -z localhost 42424; then \
		accumulo proxy -p $$ACCUMULO_HOME/proxy/proxy.properties & \
		sleep 5; \
	fi
	@cd pyaccumulo; \
	export PYTHONPATH="."; \
	echo "Create table 'analytics' (see examples/analytics.py)"; \
	echo "Execute 'python examples/analytics.py'"; \
	python examples/analytics.py