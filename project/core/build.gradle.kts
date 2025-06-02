import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

plugins {
  id("build-conventions") version "1.0"
  alias(libs.plugins.shadow)
}

dependencies {
  implementation(project(":module:bootstrap"))
}

// Fat Jar Config
tasks.shadowJar {
  dependencyFilter.exclude { it.moduleGroup == "commons-logging" }

  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("vdi-service.jar")

  manifest {
    attributes["Main-Class"]   = "vdi.bootstrap.Main"
    attributes["Git-Tag"]      = properties["build.git.tag"] ?: "unknown"
    attributes["Git-Commit"]   = properties["build.git.commit"] ?: "unknown"
    attributes["Git-Branch"]   = properties["build.git.branch"] ?: "unknown"
    attributes["Git-URL"]      = properties["build.git.url"] ?: "unknown"
    attributes["Build-ID"]     = properties["build.ci.id"] ?: "unknown"
    attributes["Build-Number"] = properties["build.ci.number"] ?: "unknown"
    attributes["Build-Time"]   = properties["build.ci.timestamp"] ?: ZonedDateTime.now(ZoneOffset.UTC)
      .format(DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .appendOffsetId()
        .toFormatter())
  }
}

tasks.register("download-dependencies") {
  doFirst {
    configurations {
      create("download") {
        project.versionCatalogs.forEach { catalog ->
          catalog.libraryAliases.forEach { libName ->
            dependencies.addLater(catalog.findLibrary(libName).get())
          }
        }

        this.files
      }
    }
  }
}

tasks.register("generate-raml-docs") {
  val mainDocsDir  = rootDir.parentFile.resolve("docs")
  val mainDocsFile = mainDocsDir.resolve("vdi-api.html")

  val restModule  = project(":module:rest-service")
  val restDocsDir = restModule.projectDir.resolve("docs")

  dependsOn(":module:rest-service:raml-docs")

  inputs.sourceFiles.files.add(restModule.projectDir.resolve("api-schema/types/library.raml"))
  outputs.files(mainDocsFile)

  doLast {

    // ensure the repo root docs dir exists.
    mainDocsDir.mkdir()

    val docFiles = arrayOf(
      // Source File to Target File
      restDocsDir.resolve("api.html") to mainDocsFile,
    )

    for ((source, target) in docFiles) {
      target.delete()
      source.copyTo(target)
      source.delete()
    }

    // drop the empty inner docs dir
    restDocsDir.deleteRecursively()
  }
}

tasks.register("compile-design-doc") {
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
      errorStream.bufferedReader().use { it.lines().forEach(::println) }
      if (waitFor() != 0) {
        throw RuntimeException("ASCIIDoctor command execution failed.")
      }
    }
  }
}
