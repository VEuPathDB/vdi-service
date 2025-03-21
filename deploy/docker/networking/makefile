DOCKER_ENV_VARS := DOCKER_HOST=unix://$${XDG_RUNTIME_DIR}/docker.sock

.PHONY: default
default:
	@echo "nope"

.PHONY: up
up:
	$(DOCKER_ENV_VARS) docker compose up

.PHONY: down
down:
	$(DOCKER_ENV_VARS) docker compose down
