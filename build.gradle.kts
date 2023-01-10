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