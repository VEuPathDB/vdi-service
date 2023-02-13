plugins {
  kotlin("jvm") version "1.8.0"
}

dependencies {
  implementation(project(":service:components:json"))

  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("io.foxcapades.lib:k-multipart:1.2.0")
}