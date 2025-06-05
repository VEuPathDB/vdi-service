rootProject.name = "vdi"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
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
