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

include(":platform")

include(":components:app-db")
include(":components:async")
include(":components:cache-db")
include(":components:dataset-reinstaller")
include(":components:env")
include(":components:handler-client")
include(":components:install-cleanup")
include(":components:kafka")
include(":components:ldap")
include(":components:plugin-mapping")
include(":components:metrics")
include(":components:module-core")
include(":components:pruner")
include(":components:rabbit")
include(":components:s3")
include(":components:test-utils")

include(":daemons:dataset-reinstaller")
include(":daemons:event-router")
include(":daemons:pruner")
include(":daemons:rest-service")
include(":daemons:reconciler")

include(":lanes:hard-delete")
include(":lanes:import")
include(":lanes:install")
include(":lanes:reconciliation")
include(":lanes:sharing")
include(":lanes:soft-delete")
include(":lanes:update-meta")
