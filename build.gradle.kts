import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.shadow)
  id("cz.alenkacz.gradle.jsonvalidator") version "1.2.1"
  id("com.ascert.open.json2yaml") version "1.1.1"
}
val jsonSchemaBuildDir = layout.buildDirectory.dir("json-schema").get().asFile

allprojects {

  configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
  }

  repositories {
    mavenLocal()
    mavenCentral()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
        password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
      }
    }
  }

  tasks.withType<KotlinCompile> {
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }

  tasks.withType<Test> {
    testLogging {
      events(
        TestLogEvent.FAILED,
        TestLogEvent.SKIPPED,
        TestLogEvent.STANDARD_OUT,
        TestLogEvent.STANDARD_ERROR,
        TestLogEvent.PASSED
      )

      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
    }
  }
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
