_COLOR := $(shell echo "\\033[38;5;69m")
_RESET := $(shell echo "\\033[0m")

ifneq '$(shell command -v podman 2>&1 >/dev/null; echo $?)' '0'
CONTAINER_CMD := podman
CONTAINER_PREFIX := vdi_
CONTAINER_SUFFIX := _1
MERGED_COMPOSE_FILES := /tmp/$(shell basename $(shell pwd))-merged-compose.yml
MERGE_TARGET := __MERGE_COMPOSE
else
CONTAINER_CMD := docker
# FIXME: Incorrect prefix for docker compose
# This was based on the directory name, but with a name now defined in the
# compose files, it will likely change.  Either set it based on the current
# directory name (if that's what docker compose does) or use the name from the
# compose.dev file.
CONTAINER_PREFIX := vdi-service-
CONTAINER_SUFFIX := -1
MERGED_COMPOSE_FILES := docker-compose.yml docker-compose.dev.yml docker-compose.ssh.yml
MERGE_TARGET :=
endif

MERGED_COMPOSE_FLAGS := $(foreach file,$(MERGED_COMPOSE_FILES),-f $(file))



.PHONY: default
default:
	@echo "Please pick a make target."


####
##  Component Building
####

.PHONY: build
build:
	@$(CONTAINER_CMD) compose \
      -f docker-compose.local.yml \
      -f docker-compose.dev.yml \
      build \
      --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
      --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

.PHONY: raml-gen
raml-gen:
	@which node || (echo 'NodeJS not found on $$PATH'; exit 1)
	@./gradlew -q :service:rest-service:generate-jaxrs --rerun-tasks
	@./gradlew -q generate-raml-docs --rerun-tasks


####
##  Stack Management
####

.PHONY: config
config: env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) config

.PHONY: up
up: env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) up -d

.PHONY: down
down: env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) down -v

.PHONY: start
start: env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) start

.PHONY: stop
stop: env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) stop

.PHONY: __MERGE_COMPOSE
__MERGE_COMPOSE:
	@vpdb merge-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.ssh.yml > $(MERGED_COMPOSE_FILES)


####
##  Logging
####

.PHONY: log-service
log-service:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)service$(CONTAINER_SUFFIX)

.PHONY: log-plugin-noop
log-plugin-noop:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-example$(CONTAINER_SUFFIX)

.PHONY: log-plugin-genelist
log-plugin-genelist:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-genelist$(CONTAINER_SUFFIX)

.PHONY: log-plugin-rnaseq
log-plugin-rnaseq:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-rnaseq$(CONTAINER_SUFFIX)

.PHONY: log-plugin-isasimple
log-plugin-isasimple:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-isasimple$(CONTAINER_SUFFIX)

.PHONY: log-plugin-biom
log-plugin-biom:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-biom$(CONTAINER_SUFFIX)

.PHONY: log-plugin-bigwig
log-plugin-bigwig:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)plugin-bigwig$(CONTAINER_SUFFIX)

.PHONY: log-kafka
log-kafka:
	@$(CONTAINER_CMD) logs -f $(CONTAINER_PREFIX)kafka$(CONTAINER_SUFFIX)

.PHONY: log-minio
log-minio:
	@$(CONTAINER_CMD) logs -f $(PROJECT_PREFIX)minio-external$(CONTAINER_SUFFIX)

.PHONY: log-rabbit
log-rabbit:
	@$(CONTAINER_CMD) logs -f $(PROJECT_PREFIX)rabbit-external$(CONTAINER_SUFFIX)


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

