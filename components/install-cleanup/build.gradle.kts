plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:plugin-mapping"))

  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  implementation("org.slf4j:slf4j-api:1.7.36")
}