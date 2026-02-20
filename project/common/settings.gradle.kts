rootProject.name = "commons"

includeBuild("../conventions")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common")  { from(files("../../gradle/common.versions.toml")) }
  }
}

include("config", "json", "logging", "model", "stack-io", "target-db", "util")

project(":config").projectDir = file("config")
project(":json").projectDir = file("json")
project(":logging").projectDir = file("logging")
project(":model").projectDir = file("model")
project(":stack-io").projectDir = file("stack-io")
project(":target-db").projectDir = file("target-db")
project(":util").projectDir = file("util")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath("com.networknt:json-schema-validator:3.0.0")
  }
}
