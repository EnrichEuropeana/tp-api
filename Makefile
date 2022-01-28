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
