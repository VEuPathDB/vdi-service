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

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":components:module-core"))

  implementation(project(":daemons:event-router"))
  implementation(project(":daemons:reconciler"))
  implementation(project(":daemons:pruner"))
  implementation(project(":daemons:rest-service"))

  implementation(project(":lanes:hard-delete"))
  implementation(project(":lanes:import"))
  implementation(project(":lanes:install"))
  implementation(project(":lanes:reconciliation"))
  implementation(project(":lanes:sharing"))
  implementation(project(":lanes:soft-delete"))
  implementation(project(":lanes:update-meta"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.veupathdb.lib:jaxrs-container-core")

  implementation("org.slf4j:slf4j-api")
  implementation("org.slf4j:jul-to-slf4j")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")
  implementation(kotlin("stdlib-jdk8"))
}

tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")

  manifest {
    attributes["Main-Class"] = "vdi.bootstrap.Main"
  }
}

// region Custom Tasks

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
  dependsOn(":daemons:rest-service:generate-raml-docs")

  doLast {
    val restModule = project(":daemons:rest-service")
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

// endregion Custom Tasks
