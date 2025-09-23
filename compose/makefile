ENV_FILE := ${PWD}/.env
SERVICES :=
OPTIONS :=
COMMAND := --help

.PHONY: default
default:
	exit 1

.PHONY: build
build: COMMAND := build
build: OPTIONS += --build-arg GH_USERNAME=${GITHUB_USERNAME}
build: OPTIONS += --build-arg GH_TOKEN=${GITHUB_TOKEN}
build: OPTIONS += --build-arg CONFIG_FILE=config/local-dev-config.yml
build: compose

.PHONY: compose-up
up: COMMAND := up
up: compose

.PHONY: down
down: COMMAND := down
down: compose

.PHONY: start
start: COMMAND := start
start: compose

.PHONY: stop
stop: COMMAND := stop
stop: compose

.PHONY: restart
restart: COMMAND := restart
restart: compose

.PHONY: logs
logs: COMMAND := logs
logs: compose

.PHONY: compose
compose: $(ENV_FILE)
	@docker compose \
		--env-file $< \
		-f docker-compose.yml \
		-f docker-compose.dev.yml \
		$(COMMAND) $(OPTIONS) $(SERVICES)
