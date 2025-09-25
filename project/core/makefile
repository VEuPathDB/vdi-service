CONTAINER_CMD := docker --log-level ERROR
BUILD_PATHS := lib/ schema/ module/ gradle/libs.versions.toml settings.gradle.kts build.gradle.kts
GIT_DIRTY := $(shell for p in $(BUILD_PATHS); do for m in $$(git status --porcelain | cut -c4-); do if [[ "$$m" =~ ^$$p ]]; then echo -n "$$m "; fi; done; done)

SERVICES ?=

_IS_TTY := $(shell [ -t 0 ] && echo true)


.PHONY: default
default:
	@echo
	@echo "Available Commands:"
	@echo
	@awk '{ \
	  if ($$1 == "#") { \
	    $$1=""; \
	    if (ht != "") { \
	      ht=ht "\n"; \
	    } \
	    if ($$2 == "|") { \
	      $$2=" "; \
	    } \
	    ht=ht "    " $$0; \
	  } else if ($$1 == ".PHONY:") { \
	    print "  \033[94m" $$2 "\033[39m\n" ht "\n"; \
	    ht="" \
	  } else {\
	    ht="" \
	  } \
	}' <(grep -B10 '.PHONY' makefile | grep -v '[═║@]\|default\|__' | grep -E '^[.#]|$$' | grep -v '_')


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Development Tooling                                                  ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

.PHONY: build
build:
	@gradle :build

# Regenerates REST API documentation and JaxRS classes from the REST service
# api.raml file
.PHONY: raml-gen
raml-gen:
	@which node || nvm --version 2>/dev/null || (echo 'NodeJS not found on $$PATH'; exit 1)
	@./gradlew -q :module:rest-service:generate-jaxrs :generate-raml-docs --rerun-tasks


# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Docker Management                                                    ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

# Builds the VDI service docker image.
.PHONY: build-image
build-image: BUILD_ID := ${USER}-$(shell date +'%s' | cut -c5-)
build-image: GIT_COMMIT := $(shell git rev-parse HEAD)
build-image:
	@cd ../.. && $(CONTAINER_CMD) build \
	  -t veupathdb/vdi-service:latest \
	  -f project/core/Dockerfile \
      --build-arg="GH_USERNAME=${GITHUB_USERNAME}" \
      --build-arg="GH_TOKEN=${GITHUB_TOKEN}" \
      --build-arg="CONFIG_FILE=config/local-dev-config.yml" \
      --build-arg="GIT_TAG=$(shell git describe --tags 2>/dev/null || echo "snapshot")" \
      --build-arg="GIT_URL=$(shell git remote get-url origin | sed 's#:#/#;s#git@#https://#')" \
      --build-arg="GIT_BRANCH=$(shell git branch --show-current)" \
      --build-arg="GIT_COMMIT=$(GIT_COMMIT)$(if $(GIT_DIRTY),-dirty)" \
      --build-arg="BUILD_TIME=$(shell date --rfc-3339=seconds --utc)" \
      --build-arg="BUILD_ID=$(BUILD_ID)" \
      --build-arg="BUILD_NUMBER=n/a" \
      .
