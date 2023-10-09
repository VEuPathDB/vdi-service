_COLOR := $(shell echo "\\033[38;5;69m")
_RESET := $(shell echo "\\033[0m")

.PHONY: default
default:
	@echo "Targets:"
	@echo "  $(_COLOR)build$(_RESET)           = Build the docker compose stack images."
	@echo "  $(_COLOR)up$(_RESET)              = Spin up the docker compose stack in the background."
	@echo "  $(_COLOR)down$(_RESET)            = Shut down and/or destroy the docker compose stack containers"
	@echo "                    and volumes."
	@echo "  $(_COLOR)start$(_RESET)           = Start up the docker compose stack after it has been stopped."
	@echo "  $(_COLOR)log-service$(_RESET)     = Print and follow the log output for the core VDI service."
	@echo "  $(_COLOR)log-plugin-noop$(_RESET) = Print and follow the logs for the noop example plugin."
	@echo "  $(_COLOR)log-kafka$(_RESET)       = Print and follow the logs for the Kafka instance."
	@echo "  $(_COLOR)open-minio$(_RESET)      = Opens the MinIO web console in your default browser."
	@echo "  $(_COLOR)open-rabbit$(_RESET)     = Opens the RabbitMQ management console in your default"
	@echo "                    browser"

.PHONY: build
build:
	@docker compose \
	  -f docker-compose.yml \
	  -f docker-compose.dev.yml \
	  build \
	  --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
	  --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

.PHONY: up
up: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

.PHONY: down
down: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v

.PHONY: start
start: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml start

.PHONY: stop
stop: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml stop

.PHONY: log-service
log-service:
	@docker logs -f vdi-service-service-1

.PHONY: log-plugin-noop
log-plugin-noop:
	@docker logs -f vdi-service-plugin-example-1

.PHONY: log-plugin-genelist
log-plugin-genelist:
	@docker logs -f vdi-service-plugin-genelist-1

.PHONY: log-kafka
log-kafka:
	@docker logs -f vdi-service-kafka-1

.PHONY: log-minio
log-minio:
	@docker logs -f vdi-service-minio-external-1

.PHONY: open-minio
open-minio:
	@sensible-browser http://localhost:9001

.PHONY: open-rabbit
open-rabbit:
	@sensible-browser http://localhost:9002

.PHONY: env-file-test
env-file-test:
	@if [ ! -f .env ]; then echo "Missing .env file."; exit 1; fi

