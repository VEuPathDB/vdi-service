plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  implementation(project(":components:common"))
}