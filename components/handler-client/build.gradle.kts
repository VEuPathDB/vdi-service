plugins {
  kotlin("jvm") version "1.8.0"
}

dependencies {
  api(project(":components:common"))
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")
}