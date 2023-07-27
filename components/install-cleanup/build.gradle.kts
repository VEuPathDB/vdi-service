plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:plugin-mapping"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.slf4j:slf4j-api")
}