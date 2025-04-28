plugins {
  kotlin("jvm") version "2.1.20"
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.networknt:json-schema-validator:1.5.6")
}
