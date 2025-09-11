PROJECT_ROOT := ${PWD}/../
ENV_FILE := $(PROJECT_ROOT)/.env
ADDITIONAL_FILES := docker-compose.ssh.yml
SERVICES :=
OPTIONS :=
COMMAND := --help

.PHONY: default
default:
	exit 1

.PHONY: build
build: COMMAND := build
build: OPTIONS += --build-arg GH_USERNAME=${GITHUB_USERNAME} --build-arg GH_TOKEN=${GITHUB_TOKEN}
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

.PHONY: compose
compose: $(ENV_FILE) $(addprefix $(PROJECT_ROOT),$(PREREQUISITE_FILES))
	@docker compose \
		--env-file $< \
		-f docker-compose.yml \
		-f docker-compose.dev.yml \
		$(addprefix -f ,$(filter-out $<, $^)) \
		$(COMMAND) $(OPTIONS) $(SERVICES)
