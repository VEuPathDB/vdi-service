DOCKER_ENV_VARS := DOCKER_HOST=unix://$${XDG_RUNTIME_DIR}/docker.sock

.PHONY: default
default:
	@echo "nope"

.PHONY: up
up:
	$(DOCKER_ENV_VARS) docker compose -f docker-compose-1.yml up -d
	#$(DOCKER_ENV_VARS) docker compose -f docker-compose-2.yml up -d

.PHONY: down
down:
	$(DOCKER_ENV_VARS) docker compose -f docker-compose-1.yml down
	#$(DOCKER_ENV_VARS) docker compose -f docker-compose-2.yml down

.PHONY: logs
logs:
	@tail \
	  <($(DOCKER_ENV_VARS) docker compose -f docker-compose-1.yml logs)
#	  <($(DOCKER_ENV_VARS) docker compose -f docker-compose-2.yml logs)
