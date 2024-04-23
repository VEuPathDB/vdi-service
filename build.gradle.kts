import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.23"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

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
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_18)
    }
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = "18"
    targetCompatibility = "18"
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


tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")

  manifest {
    attributes["Main-Class"] = "vdi.bootstrap.Main"
  }
}

tasks.create("compile-design-doc") {
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

tasks.create("generate-raml-docs") {
  dependsOn(":service:daemon:rest-service:generate-raml-docs")

  doLast {
    val restModule = project(":service:daemon:rest-service")
    val docsDir = file("docs")
    docsDir.mkdir()

    val docFiles = arrayOf(
      // Source File to Target File
      restModule.projectDir.resolve("docs/api.html") to docsDir.resolve("vdi-api.html"),
    )

    for ((source, target) in docFiles) {
      target.delete()
      source.copyTo(target)
      source.delete()
    }
  }
}

tasks.create("compose") {
  doLast {
    val base = arrayOf(
      "docker", "compose",
      "-f", "docker-compose.yml",
      "-f", "docker-compose.dev.yml",
      "-f", "docker-compose.ssh.yml",
    )

    val command = when (findProperty("compose-target") as String?) {
      "up" -> arrayOf(*base, "up", "-d")
      "down" -> arrayOf(*base, "down", "-v")
      "start" -> arrayOf(*base, "start")
      "stop" -> arrayOf(*base, "stop")
      else -> arrayOf(*base,
        "build",
        "--build-arg=GITHUB_USERNAME=${extra["github-user"]}",
        "--build-arg=GITHUB_TOKEN=${extra["github-pass"]}"
      )
    }

    with(ProcessBuilder(*command).start()) {
      runBlocking(Dispatchers.IO) {
        launch { inputStream.bufferedReader().use { it.lines().forEach(logger::quiet) } }
        launch { errorStream.bufferedReader().use {
          it.lineSequence().filter { !it.contains("level=warning") }
            .forEach(logger::quiet)
        } }
      }
      if (waitFor() != 0)
        throw RuntimeException("docker compose command failed")
    }
  }
}

dependencies {
  implementation(project(":service:bootstrap"))
}