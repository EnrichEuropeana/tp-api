build_local:
	@echo "compiling for local"
	ant -f ./build.local.xml clean && ant -f ./build.local.xml

deploy_local: build_local
	bash ./deploy.sh local
