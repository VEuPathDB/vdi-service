rootProject.name = "core-server"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common") { from(files("../../gradle/common.versions.toml")) }
  }

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = if (extra.has("gpr.user")) extra["gpr.user"] as String else System.getenv("GITHUB_USERNAME")
        password = if (extra.has("gpr.key")) extra["gpr.key"] as String else System.getenv("GITHUB_TOKEN")
      }
    }
  }
}

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

// Core Service Libraries
arrayOf(
  "async",
  "common",
  "dataset/pruner",
  "dataset/reconciler",
  "dataset/reinstaller",
  "db/application",
  "db/common",
  "db/internal",
  "external/kafka",
  "external/ldap",
  "external/rabbit",
  "external/s3",
  "install-target",
  "module-core",
  "plugin/client",
  "plugin/registry",
  "test-utils",
).forEach { name ->
  val pName = name.replace('/', '-')
  include("lib:$pName")
  project(":lib:$pName").projectDir = file("lib/$name")
}

// Core Service Modules

arrayOf(
  "await-dependencies",
  "bootstrap",
  "daemon/event-router",
  "daemon/pruner",
  "daemon/reconciler",
  "lane/hard-delete",
  "lane/import",
  "lane/install",
  "lane/reconciliation",
  "lane/sharing",
  "lane/soft-delete",
  "lane/update-meta",
  "rest-service"
).forEach { name ->
  val pName = name.replace('/', '-')
  include("module:$pName")
  project(":module:$pName").projectDir = file("module/$name")
}
