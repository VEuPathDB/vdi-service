_COLOR := $(shell echo "\\033[38;5;69m")
_RESET := $(shell echo "\\033[0m")

.PHONY: default
default:
	@echo "Please pick a make target."


####
##  Component Building
####

.PHONY: build
build:
	@./gradlew -q compose -Pcompose-target=build

.PHONY: raml-gen
raml-gen:
	@which node || (echo 'NodeJS not found on $$PATH'; exit 1)
	@./gradlew -q :service:daemon:rest-service:generate-jaxrs --rerun-tasks
	@./gradlew -q generate-raml-docs --rerun-tasks


####
##  Stack Management
####

.PHONY: up
up: env-file-test
	@./gradlew -q compose -Pcompose-target=up

.PHONY: down
down: env-file-test
	@./gradlew -q compose -Pcompose-target=down

.PHONY: start
start: env-file-test
	@./gradlew -q compose -Pcompose-target=start

.PHONY: stop
stop: env-file-test
	@./gradlew -q compose -Pcompose-target=stop


####
##  Logging
####

.PHONY: log-service
log-service:
	@docker logs -f vdi-service-service-1

.PHONY: log-plugin-noop
log-plugin-noop:
	@docker logs -f vdi-service-plugin-example-1

.PHONY: log-plugin-genelist
log-plugin-genelist:
	@docker logs -f vdi-service-plugin-genelist-1

.PHONY: log-plugin-rnaseq
log-plugin-rnaseq:
	@docker logs -f vdi-service-plugin-rnaseq-1

.PHONY: log-plugin-isasimple
log-plugin-isasimple:
	@docker logs -f vdi-service-plugin-isasimple-1

.PHONY: log-plugin-biom
log-plugin-biom:
	@docker logs -f vdi-service-plugin-biom-1

.PHONY: log-plugin-bigwig
log-plugin-bigwig:
	@docker logs -f vdi-service-plugin-bigwig-1

.PHONY: log-kafka
log-kafka:
	@docker logs -f vdi-service-kafka-1

.PHONY: log-minio
log-minio:
	@docker logs -f vdi-service-minio-external-1

.PHONY: log-rabbit
log-rabbit:
	@docker logs -f vdi-service-rabbit-external-1


####
##  Console Shortcuts
####

.PHONY: open-minio
open-minio:
	@sensible-browser http://localhost:9001

.PHONY: open-rabbit
open-rabbit:
	@sensible-browser http://localhost:9002


####
##  Helpers
####

.PHONY: env-file-test
env-file-test:
	@if [ ! -f .env ]; then echo "Missing .env file."; exit 1; fi

