default:
	@echo "what are you doing?"

build:
	@docker build -t veupathdb/vdi-internal-db:latest .

run:
	@docker run -it --rm \
		-p 5432:5432 \
		-e POSTGRES_USER=someUser \
		-e POSTGRES_PASSWORD=somePassword \
		-e POSTGRES_DB=vdi \
		veupathdb/vdi-internal-db:latest
