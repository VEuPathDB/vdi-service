plugins {
  kotlin("jvm") version "1.8.0"
}

kotlin {
  this.jvmToolchain(18)
}

java {
}

dependencies {
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
}

tasks.test {
  useJUnitPlatform()
}

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
