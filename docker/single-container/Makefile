.DEFAULT_GOAL=help
.PHONY: help cross-language
PORT_THRIFT_SERVER=42424

help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

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

