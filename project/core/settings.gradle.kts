rootProject.name = "core-server"

includeBuild("../conventions")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common") { from(files("../../gradle/common.versions.toml")) }
  }
}

pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = if (extra.has("github.username")) extra["github.username"] as String else System.getenv("GH_USERNAME")
        password = if (extra.has("github.token")) extra["github.token"] as String else System.getenv("GH_TOKEN")
      }
    }
  }
}

// Core Service Libraries
arrayOf(
  "async",
  "common",
  "config",
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
