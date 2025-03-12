_COLOR := $(shell echo "\\033[38;5;69m")
_RESET := $(shell echo "\\033[0m")

ifeq ($(shell command -v docker 2>&1 >/dev/null && echo 0),0)
CONTAINER_CMD := docker --log-level ERROR
MERGED_COMPOSE_FILES := docker-compose.yml docker-compose.dev.yml docker-compose.ssh.yml
MERGE_TARGET :=
else
CONTAINER_CMD := podman
MERGED_COMPOSE_FILES := /tmp/$(shell basename $(shell pwd))-merged-compose.yml
MERGE_TARGET := __MERGE_COMPOSE
endif

MERGED_COMPOSE_FLAGS := $(foreach file,$(MERGED_COMPOSE_FILES),-f $(file))

SERVICES ?=

_IS_TTY := $(shell [ -t 0 ] && echo true)

ifdef _IS_TTY
LOG_FLAGS ?= -f
endif


.PHONY: default
default:
	@echo $(_IS_TTY)
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
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) up -d $(SERVICES)

.PHONY: down
down: env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) down -v $(SERVICES)

.PHONY: start
start: env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) start $(SERVICES)

.PHONY: stop
stop: env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) stop $(SERVICES)

.PHONY: __MERGE_COMPOSE
__MERGE_COMPOSE:
	@vpdb merge-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.ssh.yml > $(MERGED_COMPOSE_FILES)


####
##  Logging
####

.PHONY: logs
logs:
	$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) $(SERVICES)

.PHONY: log-service
log-service:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) service

.PHONY: log-plugin-noop
log-plugin-noop:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-example

.PHONY: log-plugin-genelist
log-plugin-genelist:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-genelist

.PHONY: log-plugin-rnaseq
log-plugin-rnaseq:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-rnaseq

.PHONY: log-plugin-isasimple
log-plugin-isasimple:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-isasimple

.PHONY: log-plugin-biom
log-plugin-biom:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-biom

.PHONY: log-plugin-bigwig
log-plugin-bigwig:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-bigwig

.PHONY: log-kafka
log-kafka:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) kafka

.PHONY: log-minio
log-minio:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) minio-external

.PHONY: log-rabbit
log-rabbit:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) rabbit-external


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

