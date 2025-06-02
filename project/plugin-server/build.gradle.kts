plugins {
  id("build-conventions")
  alias(libs.plugins.shadow)
}

version = "8.2.0-beta.5"

dependencies {
  implementation(common.db.target)
  implementation(common.config)
  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)
  implementation(common.util)

  implementation(libs.metrics.micrometer)
  implementation(libs.metrics.prometheus)

  implementation(libs.ktor.core)
  implementation(libs.ktor.netty)
  implementation(libs.ktor.metrics)

  implementation(libs.log.slf4j.api)
  implementation(libs.log.log4j.slf4j)

  testImplementation(libs.bundles.testing)
  testRuntimeOnly(libs.junit.engine)
}

tasks.jar {
  enabled = false
}

tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")

  manifest {
    attributes(mapOf("Main-Class" to "vdi.MainKt"))
  }
}

tasks.register("generate-raml-docs") {
  doLast {
    val outputFile = rootDir.resolve("docs/http-api.html")
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
