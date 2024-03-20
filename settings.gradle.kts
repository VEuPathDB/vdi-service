pluginManagement {
  fun requireNonBlankGitHubCred(key: String, env: String) : String {

    // First try and load the gradle.properties entry.
    if (extra.has(key)) {
      val cred = extra[key] as String?

      // Well, they defined it, but they did it wrong, so throw an error here.
      if (cred.isNullOrBlank()) {
        println("Gradle property \"$key\" has been set to a null or blank value.")
        throw RuntimeException("Gradle property \"$key\" has been set to a null or blank value.")
      }

      return cred
    }

    // They don't have a gradle.properties entry.  Check the environment.

    val cred = System.getenv(env)

    if (cred.isNullOrBlank()) {
      println("Environment variable \"$env\" is missing or blank.")
      throw RuntimeException("Environment variable \"$env\" is missing or blank.")
    }

    return cred
  }

  fun requireGitHubUsername() = requireNonBlankGitHubCred("gpr.user", "GITHUB_USERNAME")
  fun requireGitHubToken() = requireNonBlankGitHubCred("gpr.key", "GITHUB_TOKEN")

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = requireGitHubUsername()
        password = requireGitHubToken()
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

include(":modules:dataset-reinstaller")
include(":modules:event-router")
include(":modules:pruner")
include(":modules:rest-service")
include(":modules:reconciler")

include(":modules:reconciliation-event-handler")
include(":modules:hard-delete-event-handler")
include(":modules:import-event-handler")
include(":modules:install-event-handler")
include(":modules:share-event-handler")
include(":modules:soft-delete-event-handler")
include(":modules:update-meta-event-handler")
