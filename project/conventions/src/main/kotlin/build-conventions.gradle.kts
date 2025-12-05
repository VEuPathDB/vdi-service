import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins { kotlin("jvm") }

repositories {
  mavenCentral()
  maven {
    name = "Internal Packages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("github.username")) extra["github.username"] as String? else System.getenv("GH_USERNAME")
      password = if (extra.has("github.token")) extra["github.token"] as String? else System.getenv("GH_TOKEN")
    }
  }
}

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(24)
  }
  compilerOptions {
    progressiveMode = true
    extraWarnings = true

    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
  }
}

val libs = the<LibrariesForLibs>()

dependencies {
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
  testRuntimeOnly(libs.junit.launcher)
  testRuntimeOnly(libs.junit.compat)
  testRuntimeOnly(libs.junit.engine)
}

tasks.withType<Test> {
  useJUnitPlatform()

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
