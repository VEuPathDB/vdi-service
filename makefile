CONTAINER_CMD := docker --log-level ERROR

SERVICES ?=

_IS_TTY := $(shell [ -t 0 ] && echo true)


.PHONY: default
default:
	@echo
	@echo "Available Commands:"
	@echo
	@awk '{ \
	  if ($$1 == "#") { \
	    $$1=""; \
	    if (ht != "") { \
	      ht=ht "\n"; \
	    } \
	    if ($$2 == "|") { \
	      $$2=" "; \
	    } \
	    ht=ht "    " $$0; \
	  } else if ($$1 == ".PHONY:") { \
	    print "  \033[94m" $$2 "\033[39m\n" ht "\n"; \
	    ht="" \
	  } else {\
	    ht="" \
	  } \
	}' <(grep -B10 '.PHONY' makefile | grep -v '[═║@]\|default\|__' | grep -E '^[.#]|$$' | grep -v '_')


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Stack Management Tasks                                               ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

ALPINE_DEV_BASE_TAG := jdk24-gradle8.14
STACK_CONFIG_PATH := config/production-config.yml

# Validates the VDI configuration file against the config JSON schema using the
# VPDB java dev container.
#
# See: validate-config-local
.PHONY: validate-config
validate-config:
	@echo "running validation in dev-build container"
	@echo
	@$(CONTAINER_CMD) run \
	  --rm \
	  --volume $$PWD/service/schema/config:/vdi/config/schema \
	  --volume $$PWD/tools/config-validator:/vdi/config/validator \
	  --volume $$PWD/config:/vdi/config \
	  --workdir /vdi/config/validator \
	  veupathdb/alpine-dev-base:jdk24-gradle8.14 \
	  gradle validate-config \
	    --quiet \
	    -Pconfig-file=/vdi/$(STACK_CONFIG_PATH) \
	    -Pschema-file=/vdi/config/schema/stack-config.json

# Validates the VDI configuration file against the config JSON schema using a
# local JVM.
#
# See: validate-config
.PHONY: validate-config
validate-config-local:
	@gradle :config-validator:validate-config \
	  --quiet \
	  -Pconfig-file=$$PWD/$(STACK_CONFIG_PATH) \
	  -Pschema-file=$$PWD/service/schema/config/stack-config.json


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Development Tooling                                                  ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #


.PHONY: build
build:
	@gradle :service:build

# Regenerates REST API documentation and JaxRS classes from the REST service
# api.raml file
.PHONY: raml-gen
raml-gen:
	@which node || (echo 'NodeJS not found on $$PATH'; exit 1)
	@./gradlew -q :service:module:rest-service:generate-jaxrs :service:generate-raml-docs --rerun-tasks

####
##  Console Shortcuts
####

# Opens the local-dev minio web console.
.PHONY: open-minio
open-minio:
	@sensible-browser http://localhost:9001

# Opens the local-dev rabbitmq web console.
.PHONY: open-rabbit
open-rabbit:
	@sensible-browser http://localhost:9002

####
##  Helpers
####


.PHONY: _env-file-test
_env-file-test:
	@if [ ! -f compose/.env ]; then echo "Missing .env file."; exit 1; fi


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Docker Management                                                    ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

ENV_FILE := ${PWD}/.env

COMPOSE_DIR   := compose
COMPOSE_FILES := docker-compose.yml docker-compose.dev.yml docker-compose.ssh.yml

MERGED_COMPOSE_FLAGS := $(foreach FILE,$(COMPOSE_FILES),-f $(COMPOSE_DIR)/$(FILE))

COMPOSE_CMD   := $(CONTAINER_CMD) compose --env-file=$(ENV_FILE) $(MERGED_COMPOSE_FLAGS)

# Builds the VDI service docker image.
.PHONY: build-image
build-image:
	@$(CONTAINER_CMD) build \
	  -t veupathdb/vdi-service:latest \
	  -f service/Dockerfile \
      --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
      --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN} \
      --build-arg=CONFIG_FILE=config/local-dev-config.yml \
      .

# Prints the merged docker compose configuration.
.PHONY: show-compose-config
show-compose-config: _env-file-test $(MERGE_TARGET)
	@$(COMPOSE_CMD) config

# Starts up the stack (re)creating any containers necessary.
.PHONY: compose-up
compose-up: _env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(COMPOSE_CMD) up -d $(SERVICES)

# Stops the stack and deletes any stack-specific state.
.PHONY: compose-down
compose-down: _env-file-test $(MERGE_TARGET)
	@$(COMPOSE_CMD) down -v $(SERVICES)

# Starts a stack that has been shut down via `compose-stop`
.PHONY: compose-start
compose-start: _env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(COMPOSE_CMD) start $(SERVICES)

# Stops a started stack without removing stack-specific state.
.PHONY: compose-stop
compose-stop: _env-file-test $(MERGE_TARGET)
	@$(COMPOSE_CMD) stop $(SERVICES)

####
##  Helpers
####

ifdef _IS_TTY
LOG_FLAGS ?= -f
endif

COMPOSE_LOG_CMD := $(COMPOSE_CMD) logs $(LOG_FLAGS)

# Prints and watches the full compose stack log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
#
# Specific services may be specified by using the SERVICES variable.
.PHONY: logs
logs:
	$(COMPOSE_LOG_CMD) $(SERVICES)

# Prints and tails the core service container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-service
log-service: SERVICES := service
log-service: logs

# Prints and tails the internal database container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-database
log-database: SERVICES := cache-db
log-database: logs

# Prints and tails the example plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-example
log-plugin-example: SERVICES := plugin-example
log-plugin-example: logs

# Prints and tails the noop plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-noop
log-plugin-noop:
	@$(COMPOSE_LOG_CMD) plugin-noop

# Prints and tails the genelist plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-genelist
log-plugin-genelist:
	@$(COMPOSE_LOG_CMD) plugin-genelist

# Prints and tails the rnaseq plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-rnaseq
log-plugin-rnaseq:
	@$(COMPOSE_LOG_CMD) plugin-rnaseq

# Prints and tails the isasimple plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-isasimple
log-plugin-isasimple:
	@$(COMPOSE_LOG_CMD) plugin-isasimple

# Prints and tails the biom plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-biom
log-plugin-biom:
	@$(COMPOSE_LOG_CMD) plugin-biom

# Prints and tails the bigwig plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-bigwig
log-plugin-bigwig:
	@$(COMPOSE_LOG_CMD) plugin-bigwig

# Prints and tails the phenotype plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-wrangler
log-plugin-phenotype:
	@$(COMPOSE_LOG_CMD) plugin-phenotype

# Prints and tails the kafka log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-kafka
log-kafka:
	@$(COMPOSE_LOG_CMD) kafka

# Prints and tails the local minio container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-minio
log-minio:
	@$(COMPOSE_LOG_CMD) minio-external

# Prints and tails the local rabbitmq container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-rabbit
log-rabbit:
	@$(COMPOSE_LOG_CMD) rabbit-external


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Workflow Tasks                                                       ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

OUTPUT_DOC_DIR := "build/generated-docs"

.PHONY: generate-service-docs
generate-service-docs:
	@mkdir -p $(OUTPUT_DOC_DIR)/schema/data $(OUTPUT_DOC_DIR)/schema/config
	@gradle \
		--no-daemon \
		:service:generate-raml-docs \
		:service:schema:build-dataset-schema-resources \
		:service:schema:build-config-schema-resource
	@cp -rt $(OUTPUT_DOC_DIR) \
		docs/vdi-api.html \
		service/schema \
		service/build/json-schema/*
	@python -m venv venv
	@. venv/bin/activate \
		&& pip install json-schema-for-humans \
		&& generate-schema-doc \
			--config expand_buttons \
			--config examples_as_yaml \
			--config no_link_to_reused_ref \
			service/schema/config/stack-config.json \
			$(OUTPUT_DOC_DIR)/config-schema.html
