import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.21"
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
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-s3:1.2.0-SNAPSHOT") { isChanging = true }


  implementation(project(":modules:event-router"))
  implementation(project(":modules:import-trigger-handler"))
  implementation(project(":modules:update-meta-trigger-handler"))
  implementation(project(":modules:rest-service"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

  implementation("org.veupathdb.lib.s3:s34k-minio:0.4.0+s34k-0.8.0")

  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-api:2.20.0")
  implementation("org.apache.logging.log4j:log4j-core:2.20.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
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
  dependsOn(":modules:rest-service:generate-raml-docs")

  doLast {
    val restModule = project(":modules:rest-service")
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
