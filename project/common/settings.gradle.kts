rootProject.name = "commons"

includeBuild("../conventions")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common")  { from(files("../../gradle/common.versions.toml")) }
  }
}

include("target-db", "config", "json", "logging", "model", "tmp", "util")

project(":target-db").projectDir = file("target-db")
project(":config").projectDir = file("config")
project(":json").projectDir = file("json")
project(":logging").projectDir = file("logging")
project(":model").projectDir = file("model")
project(":tmp").projectDir = file("tmp")
project(":util").projectDir = file("util")
