pluginManagement {
  fun requireNonBlankGitHubCred(key: String, env: String) =
    if (extra.has(key)) {
      with(extra[key] as String?) {
        if (isNullOrBlank()) {
          println("Gradle property \"$key\" has been set to a null or blank value.")
          throw RuntimeException("Gradle property \"$key\" has been set to a null or blank value.")
        }

        this
      }
    } else {
      with(System.getenv(env)) {
        if (isNullOrBlank()) {
          println("Environment variable \"$env\" is missing or blank and no \"$key\" gradle property was present.")
          throw RuntimeException("Environment variable \"$env\" is missing or blank and no \"$key\" gradle property was present.")
        }

        this
      }
    }

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = requireNonBlankGitHubCred("gpr.user", "GITHUB_USERNAME")
        password = requireNonBlankGitHubCred("gpr.key", "GITHUB_TOKEN")
      }
    }
  }
}

rootProject.name = "vdi"

include(":lib:common")

include(":lib:db:application")
include(":lib:db:common")
include(":lib:db:internal")

include(":lib:dataset:reinstaller")
include(":lib:dataset:pruner")
include(":lib:dataset:reconciler")

include(":lib:external:ldap")
include(":lib:external:rabbit")
include(":lib:external:s3")

include(":lib:plugin:client")
include(":lib:plugin:registry")

include(":lib:async")
include(":lib:kafka")
include(":lib:module-core")
include(":lib:test-utils")

include(":service:bootstrap")
include(":service:rest-service")

include(":service:daemon:event-router")
include(":service:daemon:pruner")
include(":service:daemon:reconciler")

include(":service:lane:hard-delete")
include(":service:lane:import")
include(":service:lane:install")
include(":service:lane:reconciliation")
include(":service:lane:sharing")
include(":service:lane:soft-delete")
include(":service:lane:update-meta")
