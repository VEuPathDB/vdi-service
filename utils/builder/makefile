.PHONY: build
build:
	@mkdir -p bin/darwin bin/linux
	@go mod tidy
	@env CGO_ENABLED=0 GOOS=linux go build -o bin/linux/builder cmd/main.go
	@env CGO_ENABLED=0 GOOS=darwin go build -o bin/darwin/builder cmd/main.go
