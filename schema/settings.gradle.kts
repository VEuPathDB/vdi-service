rootProject.name = "schemata"

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
    classpath("com.networknt:json-schema-validator:1.5.8")
  }
}
