default:
	@echo "what are you doing?"

build:
	@docker compose \
	        -f docker-compose.yml \
	        -f docker-compose.dev.yml \
	        build \
	        --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
	        --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

up: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

down: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v

start: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml start

stop: env-file-test
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml stop

log-service:
	@docker logs -f vdi-service-service-1

log-plugin-noop:
	@docker logs -f vdi-service-plugin-example-1

log-kafka:
	@docker logs -f vdi-service-kafka-1

open-minio:
	@sensible-browser http://localhost:9001

open-rabbit:
	@sensible-browser http://localhost:9002

env-file-test:
	@if [ ! -f .env ]; then echo "Missing .env file."; exit 1; fi

