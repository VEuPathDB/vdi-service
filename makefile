CONTAINER_CMD := docker --log-level ERROR

COMPOSE_DIR   := compose
COMPOSE_FILES := docker-compose.yml docker-compose.dev.yml docker-compose.ssh.yml

SERVICES ?=

_IS_TTY := $(shell [ -t 0 ] && echo true)

ifdef _IS_TTY
LOG_FLAGS ?= -f
endif


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


# Builds the VDI service docker image.
.PHONY: build-image
build-image:
	@$(CONTAINER_CMD) build \
	  -t veupathdb/vdi-service:latest \
	  -f service/Dockerfile \
      --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
      --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN} \
      --build-arg=CONFIG_FILE=config/halfway-config.yml \
      .

MERGED_COMPOSE_FLAGS := $(foreach FILE,$(COMPOSE_FILES),-f $(COMPOSE_DIR)/$(FILE))

# Prints the merged docker compose configuration.
.PHONY: show-compose-config
show-compose-config: _env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) config

# Starts up the stack (re)creating any containers necessary.
.PHONY: compose-up
compose-up: _env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) up -d $(SERVICES)

# Stops the stack and deletes any stack-specific state.
.PHONY: compose-down
compose-down: _env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) down -v $(SERVICES)

# Starts a stack that has been shut down via `compose-stop`
.PHONY: compose-start
compose-start: _env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) start $(SERVICES)

# Stops a started stack without removing stack-specific state.
.PHONY: compose-stop
compose-stop: _env-file-test $(MERGE_TARGET)
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) stop $(SERVICES)

####
##  Helpers
####

# Prints and watches the full compose stack log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
#
# Specific services may be specified by using the SERVICES variable.
.PHONY: logs
logs:
	$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) $(SERVICES)

# Prints and tails the core service container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-service
log-service:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) service

# Prints and tails the internal database container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-database
log-database:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) cache-db

# Prints and tails the example plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-example
log-plugin-example:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-example

# Prints and tails the noop plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-noop
log-plugin-noop:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-noop

# Prints and tails the genelist plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-genelist
log-plugin-genelist:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-genelist

# Prints and tails the rnaseq plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-rnaseq
log-plugin-rnaseq:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-rnaseq

# Prints and tails the isasimple plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-isasimple
log-plugin-isasimple:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-isasimple

# Prints and tails the biom plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-biom
log-plugin-biom:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-biom

# Prints and tails the bigwig plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-bigwig
log-plugin-bigwig:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-bigwig

# Prints and tails the phenotype plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-wrangler
log-plugin-phenotype:
	@(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) plugin-phenotype

# Prints and tails the kafka log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-kafka
log-kafka:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) kafka

# Prints and tails the local minio container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-minio
log-minio:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) minio-external

# Prints and tails the local rabbitmq container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-rabbit
log-rabbit:
	@$(CONTAINER_CMD) compose $(MERGED_COMPOSE_FLAGS) logs $(LOG_FLAGS) rabbit-external


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
		:service:build-dataset-schema-resources \
		:service:build-config-schema-resource
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
