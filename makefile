GRADLE := $(shell command -v gradle || echo "./gradlew")

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

.PHONY: build-plugin-server
build-plugin-server:
	@$(GRADLE) :plugin-server:build

.PHONY: build-core-server
build-core-server:
	@$(GRADLE) :core:build
