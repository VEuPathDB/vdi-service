rootProject.name = "vdi"

dependencyResolutionManagement {
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
}

includeBuild("project/core")

includeBuild("project/common") {
  dependencySubstitution {
    substitute(module("common:target-db")).using(project(":target-db"))
    substitute(module("common:config")).using(project(":config"))
    substitute(module("common:json")).using(project(":json"))
    substitute(module("common:logging")).using(project(":logging"))
    substitute(module("common:model")).using(project(":model"))
    substitute(module("common:util")).using(project(":util"))
  }
}

if (file("project/plugin-server").exists())
  includeBuild("project/plugin-server")

includeBuild("schema") {
  dependencySubstitution { substitute(module("vdi:schemata")).using(project(":")) }
}

includeBuild("stack-db/migrations") {
  dependencySubstitution { substitute(module("db:migrations")).using(project(":")) }
}