tasks.create("compile-design-doc") {
  group = "monorepo"

  doLast {
    val command = arrayOf(
      "asciidoctor",
      "--backend", "html5",
      "--out-file", "docs/design/1.0/design.html",
      "docs/design/1.0/design.adoc"
    )

    println("Running ASCIIDoctor")
    println(command.joinToString(" "))

    with(ProcessBuilder(*command).start()) {
      if (waitFor() != 0) {
        errorStream.bufferedReader().use { it.lines().forEach(::println) }
        throw RuntimeException("ASCIIDoctor command execution failed.")
      }
    }
  }
}

tasks.create("generate-raml-docs") {
  group = "monorepo"

  dependsOn(":service-vdi:generate-raml-docs")

  doLast {
    val docsDir = file("docs")
    val vdiDocsFile =  file("service-vdi/docs/api.html")

    docsDir.mkdir()

    with(docsDir.resolve("vdi-api.html")) {
      delete()
      vdiDocsFile.copyTo(this)
    }
  }
}