import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

plugins {
  id("build-conventions")
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

    attributes(
      mapOf(
        "Git-Tag" to (findProperty("build.git.tag") as String? ?: "unknown"),
        "Git-Commit" to (findProperty("build.git.commit") as String? ?: "unknown"),
        "Git-Branch" to (findProperty("build.git.branch") as String? ?: "unknown"),
        "Git-URL" to (findProperty("build.git.url") as String? ?: "unknown"),
        "Build-ID" to (findProperty("build.ci.id") as String? ?: "unknown"),
        "Build-Number" to (findProperty("build.ci.number") as String? ?: "unknown"),
        "Build-Time" to (findProperty("build.ci.timestamp") as String? ?: ZonedDateTime.now(ZoneOffset.UTC)
          .format(DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendOffsetId()
            .toFormatter()))
      ),
      "build-info"
    )
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
  val mainDocsDir  = findProperty("DOC_BUILD_DIR")?.let { File(it as String) }
    ?: rootDir.parentFile.resolve("docs")
  val mainDocsFile = findProperty("DOC_FILE_NAME")?.let{ mainDocsDir.resolve(it as String) }
    ?: mainDocsDir.resolve("vdi-api.html")

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
