default:
	@echo "what are you doing?"

compose-build:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml build

compose-up:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up

compose-down:
	@docker compose -f docker-compose.yml -f docker-compose.dev.yml down