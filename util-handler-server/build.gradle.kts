tasks.create("generate-raml-docs") {
  doLast {
    val outputFile = file("api.html")
    outputFile.delete()
    outputFile.createNewFile()

    outputFile.outputStream().use { out ->
      with(
        ProcessBuilder(
          "raml2html",
          "api.raml",
          "--theme", "raml2html-modern-theme"
        )
          .directory(projectDir)
          .start()
      ) {
        inputStream.transferTo(out)
        errorStream.transferTo(System.err)

        if (waitFor() != 0) {
          throw RuntimeException("raml2html process failed")
        }
      }
    }
  }
}

/**
 * old generation of installer that installs eda studies into an eda database
 *
 * it does the same work as the workflow
 *
 * it invokes a system called next-flow? which is a container orchestration
 * system
 *
 * how do we run containers in a container?
 *
 * nextflow as a service?
 */