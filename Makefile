pwd := $(shell pwd)
dbPath := ../../transcribathon-platform/tp-mysql

docker_logs:
	sudo docker-compose logs

docker_start:
	@echo "Starting the database container..."
	cd $(dbPath) && sudo docker-compose up -d
	@echo "Starting the tomcat container..."
	cd $(pwd) && sudo docker-compose up -d

docker_stop:
	@echo "Stopping all container..."
	cd $(dbPath) && sudo docker-compose down
	cd $(pwd) && sudo docker-compose down

build_sid:
	@echo "compiling for SID"
	cd $(pwd) && sudo docker-compose down
	ant -f ./build.sid.xml clean && ant -f ./build.sid.xml
	cd $(pwd) && sudo docker-compose up -d

build_local:
	@echo "compiling for local"
	ant -f ./build.local.xml clean && ant -f ./build.local.xml

build_dev:
	@echo "compiling for dev"
	ant -f ./build.dev.xml clean && ant -f ./build.dev.xml

build_prod:
	@echo "compiling for production"
	ant -f ./build.prod.xml clean && ant -f ./build.prod.xml

deploy_local: build_local
	bash ./deploy.sh local
