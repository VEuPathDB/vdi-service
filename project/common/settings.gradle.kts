rootProject.name = "commons"

includeBuild("../conventions")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common")  { from(files("../../gradle/common.versions.toml")) }
  }
}

include("target-db", "config", "json", "logging", "model", "util")

project(":target-db").projectDir = file("target-db")
project(":config").projectDir = file("config")
project(":json").projectDir = file("json")
project(":logging").projectDir = file("logging")
project(":model").projectDir = file("model")
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
    classpath("com.networknt:json-schema-validator:2.0.0")
  }
}
