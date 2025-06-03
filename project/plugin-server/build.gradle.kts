import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper

plugins {
  id("build-conventions")
  alias(libs.plugins.shadow)
}

version = "8.2.0-beta.5"

repositories {
  maven {
    System.getenv().forEach { (k, v) ->
      System.err.println("$k  =  $v")
    }

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
    attributes(mapOf("Main-Class" to "vdi.MainKt"))
  }
}
