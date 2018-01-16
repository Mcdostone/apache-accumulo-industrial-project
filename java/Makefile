.DEFAULT_GOAL=build
.PHONY: build deploy
OUTPUT_DIR = ~/

build:
	mvn -Pprod clean compile assembly:single

deploy: build
	scp target/*.jar root@37.59.123.111:$(OUTPUT_DIR)
	scp target/*.jar root@37.59.123.118:$(OUTPUT_DIR)
	scp target/*.jar root@37.59.123.138:$(OUTPUT_DIR)