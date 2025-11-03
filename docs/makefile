.PHONY: build
build: content/plugin-scripts.html

content/plugin-scripts.html: src/adoc/plugin-scripts.adoc src/css/adoc-dark.css
	@asciidoctor -o $@ $<