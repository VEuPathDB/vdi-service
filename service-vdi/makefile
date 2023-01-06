#
# Development environment setup / teardown
#

.PHONY: install-dev-env
install-dev-env:
	./gradlew check-env

.PHONY: clean
clean:
	@./gradlew clean
	@rm -rf .gradle

#
# Build & Test Targets
#

.PHONY: compile
compile:
	./gradlew clean compileJava

.PHONY: test
test:
	./gradlew clean test

.PHONY: jar
jar: build/libs/service.jar

.PHONY: docker
docker:
	./gradlew build-docker --stacktrace

#
# Code & Doc Generation
#

.PHONY: raml-gen-code
raml-gen-code:
	./gradlew generate-jaxrs

.PHONY: raml-gen-docs
raml-gen-docs:
	./gradlew generate-raml-docs

#
# File based targets
#

build/libs/service.jar: build.gradle.kts
	./gradlew clean test generate-raml-docs shadowJar
