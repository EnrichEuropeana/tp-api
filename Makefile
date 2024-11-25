SHELL := /bin/bash
SDKMAN_INIT := $(HOME)/.sdkman/bin/sdkman-init.sh
PWD := $(shell pwd)
DB := ../../transcribathon-platform/tp-mysql
SOLR := ../../transcribathon-platform/tp-solr
SDK := . $(SDKMAN_INIT) && sdk use java 8.0.322.fx-zulu

docker_logs:
	sudo docker compose logs

docker_start:
	@echo "Starting the database container..."
	@cd $(DB) && sudo docker compose up --detach
	@echo "Starting Solr..."
	@cd $(SOLR) && sudo docker compose up --detach
	@echo "Starting the tomcat container..."
	@cd $(PWD) && sudo docker compose up --detach

docker_stop:
	@echo "Stopping all container..."
	@cd $(DB) && sudo docker compose down
	@cd $(SOLR) && sudo docker compose down
	@cd $(PWD) && sudo docker compose down

build_sid:
	@echo "compiling for SID"
	@cd $(PWD) && sudo docker compose down
	$(SDK) && ant -f ./build.sid.xml clean && ant -f ./build.sid.xml
	@cd $(PWD) && sudo docker compose up --detach

build_local:
	@echo "compiling for local"
	$(SDK) && ant -f ./build.local.xml clean && ant -f ./build.local.xml

build_dev:
	@echo "compiling for dev"
	$(SDK) && ant -f ./build.dev.xml clean && ant -f ./build.dev.xml

build_prod:
	@echo "compiling for production"
	$(SDK) && ant -f ./build.prod.xml clean && ant -f ./build.prod.xml

deploy_local: build_local
	bash ./deploy.sh local
