default:
	@echo "what are you doing?"

cb: compose-build
compose-build:
	@docker compose \
	        -f docker-compose.yml \
	        -f docker-compose.dev.yml \
	        build \
	        --build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
	        --build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

cu: compose-up
compose-up:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up

cd: compose-down
compose-down:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v
