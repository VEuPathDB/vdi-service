GRADLE := $(shell command -v gradle || echo "./gradlew")

.PHONY: default
default:
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
	}' <(grep -B10 '.PHONY' makefile | grep -v '[═║@]\|default\|__' | grep -E '^[.#]|$$' | grep -v '_') | less

#
# Local Builds
#

# Generate JaxRS-based java classes from the service's RAML API specification
.PHONY: raml-gen
raml-gen:
	@which node || nvm --version 2>/dev/null || (echo 'NodeJS not found on $$PATH'; exit 1)
	@$(GRADLE) -q :core:module:rest-service:generate-jaxrs :core:generate-raml-docs --rerun-tasks

DOC_BUILD_DIR := ${PWD}/build/generated-docs

# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Project Build Tasks                                                  ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

# Builds the VDI plugin server shadow jar.
.PHONY: build-plugin-server
build-plugin-server:
	@$(GRADLE) :plugin-server:build

# Builds the VDI core server shadow jar.
.PHONY: build-core-server
build-core-server:
	@$(GRADLE) :core:build

# Builds the VDI core server Docker image.
.PHONY: build-image
build-image:
	@echo "$$($(GRADLE) -q build-image-cmd)" | bash

# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Stack Management                                                     ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

ENV_FILE := ${PWD}/.env
COMPOSE_DIR   := compose
COMPOSE_FILES := docker-compose.yml docker-compose.dev.yml docker-compose.ssh.yml
MERGED_COMPOSE_FLAGS := $(foreach FILE,$(COMPOSE_FILES),-f $(COMPOSE_DIR)/$(FILE))
COMPOSE_CMD   := docker compose --env-file=$(ENV_FILE) $(MERGED_COMPOSE_FLAGS)

# Starts up the stack (re)creating any containers necessary.
.PHONY: compose-up
compose-up: _env-file-test $(MERGE_TARGET)
	@if [ -z "${SSH_AUTH_SOCK}" ]; then echo "SSH agent does not appear to be correctly running"; exit 1; fi
	@$(COMPOSE_CMD) up -d $(SERVICES)

# Stops the stack and deletes any stack-specific state.
.PHONY: compose-down
compose-down: _env-file-test $(MERGE_TARGET)
	@$(COMPOSE_CMD) down -v $(SERVICES)

.PHONY: _env-file-test
_env-file-test:
	@if [ ! -f .env ]; then echo "Missing .env file."; exit 1; fi

# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Stack Logging                                                        ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

_IS_TTY := $(shell [ -t 0 ] && echo true)
ifdef _IS_TTY
LOG_FLAGS ?= -f
endif

SERVICES :=
COMPOSE_LOG_CMD := $(COMPOSE_CMD) logs $(LOG_FLAGS)

# Prints and watches the full compose stack log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
#
# Specific services may be specified by using the SERVICES variable.
.PHONY: logs
logs:
	@$(COMPOSE_LOG_CMD) $(SERVICES)

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
log-plugin-noop: SERVICES := plugin-noop
log-plugin-noop: logs

# Prints and tails the genelist plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-genelist
log-plugin-genelist: SERVICES := plugin-genelist
log-plugin-genelist: logs

# Prints and tails the rnaseq plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-rnaseq
log-plugin-rnaseq: SERVICES := plugin-rnaseq
log-plugin-rnaseq: logs

# Prints and tails the isasimple plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-isasimple
log-plugin-isasimple: SERVICES := plugin-isasimple
log-plugin-isasimple: logs

# Prints and tails the biom plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-biom
log-plugin-biom: SERVICES := plugin-biom
log-plugin-biom: logs

# Prints and tails the bigwig plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-bigwig
log-plugin-bigwig: SERVICES := plugin-bigwig
log-plugin-bigwig: logs

# Prints and tails the phenotype plugin container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-plugin-wrangler
log-plugin-phenotype: SERVICES := plugin-phenotype
log-plugin-phenotype: logs

# Prints and tails the kafka log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-kafka
log-kafka: SERVICES := kafka
log-kafka: logs

# Prints and tails the local minio container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-minio
log-minio: SERVICES := minio-external
log-minio: logs

# Prints and tails the local rabbitmq container log output.
#
# Log following may be disabled by setting LOG_FLAGS to an empty string.
.PHONY: log-rabbit
log-rabbit: SERVICES := rabbit-external
log-rabbit: logs
