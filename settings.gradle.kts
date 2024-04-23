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

  extra["github-user"] = requireNonBlankGitHubCred("gpr.user", "GITHUB_USERNAME")
  extra["github-pass"] = requireNonBlankGitHubCred("gpr.key", "GITHUB_TOKEN")

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = extra["github-user"] as String
        password = extra["github-pass"] as String
      }
    }
  }
}

rootProject.name = "vdi"

include(":platform")

include(":lib:app-db")
include(":lib:async")
include(":lib:cache-db")
include(":lib:dataset-reinstaller")
include(":lib:env")
include(":lib:handler-client")
include(":lib:install-cleanup")
include(":lib:kafka")
include(":lib:ldap")
include(":lib:plugin-mapping")
include(":lib:metrics")
include(":lib:module-core")
include(":lib:pruner")
include(":lib:rabbit")
include(":lib:s3")
include(":lib:test-utils")

include(":service:bootstrap")

include(":service:daemon:event-router")
include(":service:daemon:pruner")
include(":service:daemon:rest-service")
include(":service:daemon:reconciler")

include(":service:lane:hard-delete")
include(":service:lane:import")
include(":service:lane:install")
include(":service:lane:reconciliation")
include(":service:lane:sharing")
include(":service:lane:soft-delete")
include(":service:lane:update-meta")
