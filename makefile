default:
	@echo "what are you doing?"

compose-build:
	@docker compose \
	        -f docker-compose.yml \
	        -f docker-compose.dev.yml \
	        build \
	        --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
	        --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

compose-up:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up

compose-down:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v
