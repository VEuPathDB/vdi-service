GRADLE := $(shell command -v gradle || echo "./gradlew")

.PHONY: default
default:
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
	}' <(grep -B10 '.PHONY' makefile | grep -v '[═║@]\|default\|__' | grep -E '^[.#]|$$' | grep -v '_') | less

#
# Local Builds
#

# Generate JaxRS-based java classes from the service's RAML API specification
.PHONY: raml-gen
raml-gen:
	@which node || nvm --version 2>/dev/null || (echo 'NodeJS not found on $$PATH'; exit 1)
	@$(GRADLE) -q :core:module:rest-service:generate-jaxrs :core:generate-raml-docs --rerun-tasks

# ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ #
# ┃                                                                          ┃ #
# ┃     Project Build Tasks                                                  ┃ #
# ┃                                                                          ┃ #
# ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛ #

# Builds the VDI plugin server shadow jar.
.PHONY: build-plugin-server
build-plugin-server:
	@$(GRADLE) :plugin-server:build

# Builds the VDI core server shadow jar.
.PHONY: build-core-server
build-core-server:
	@$(GRADLE) :core:build

# (Re)Builds the VDI dev stack container images.
.PHONY: build-image
build-image:
	@$(MAKE) -C project/core build-image

# (Re)Builds the VDI dev stack container images.
.PHONY: build-db
build-db:
	@$(MAKE) -C stack-db/definition build
