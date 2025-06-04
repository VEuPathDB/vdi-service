import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_21
java.targetCompatibility = JavaVersion.VERSION_21
java.sourceCompatibility = JavaVersion.VERSION_21

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
