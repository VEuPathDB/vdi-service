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

.PHONY: raml-gen
raml-gen:
	@which node || nvm --version 2>/dev/null || (echo 'NodeJS not found on $$PATH'; exit 1)
	@$(GRADLE) -q :core:module:rest-service:generate-jaxrs :core:generate-raml-docs --rerun-tasks

DOC_BUILD_DIR := ${PWD}/build/generated-docs
.PHONY: build-docs
build-docs:
	@rm -rf $(DOC_BUILD_DIR)
	@mkdir -p $(DOC_BUILD_DIR)
	@gradle -P DOC_BUILD_DIR=$(DOC_BUILD_DIR)/core -P DOC_FILE_NAME=index.html :core:generate-raml-docs
	@gradle -P SCHEMA_BUILD_DIR=$(DOC_BUILD_DIR) \
		:schema:build-dataset-schema-resources \
		:schema:build-config-schema-resource
	@generate-schema-doc \
		--config expand_buttons \
		--config examples_as_yaml \
		--config no_link_to_reused_ref \
		$(DOC_BUILD_DIR)/schema/config/full-config.json \
		$(DOC_BUILD_DIR)/schema/config/index.html
