import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

plugins {
  id("build-conventions")
  alias(libs.plugins.shadow)
}

repositories {
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("github.username")) extra["github.username"] as String else System.getenv("GH_USERNAME")
      password = if (extra.has("github.token")) extra["github.token"] as String else System.getenv("GH_TOKEN")
    }
  }
}

dependencies {
  implementation(common.db.target)
  implementation(common.config)
  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)
  implementation(common.stack.io)
  implementation(common.util)

  implementation(libs.metrics.micrometer)
  implementation(libs.metrics.prometheus)

  implementation(libs.ktor.core)
  implementation(libs.ktor.netty)
  implementation(libs.ktor.metrics)

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
    attributes(mapOf(
      "Main-Class"   to "vdi.service.plugin.MainKt",
      "Git-Tag"      to (findProperty("build.git.tag")      as String? ?: "unknown"),
      "Git-Commit"   to (findProperty("build.git.commit")   as String? ?: "unknown"),
      "Git-Branch"   to (findProperty("build.git.branch")   as String? ?: "unknown"),
      "Git-URL"      to (findProperty("build.git.url")      as String? ?: "unknown"),
      "Build-ID"     to (findProperty("build.ci.id")        as String? ?: "unknown"),
      "Build-Number" to (findProperty("build.ci.number")    as String? ?: "unknown"),
      "Build-Time"   to (findProperty("build.ci.timestamp") as String? ?: ZonedDateTime.now(ZoneOffset.UTC)
        .format(DateTimeFormatterBuilder()
          .append(DateTimeFormatter.ISO_LOCAL_DATE)
          .appendLiteral(' ')
          .appendValue(ChronoField.HOUR_OF_DAY, 2)
          .appendLiteral(':')
          .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
          .appendLiteral(':')
          .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
          .appendOffsetId()
          .toFormatter())),
    ))
  }
}
