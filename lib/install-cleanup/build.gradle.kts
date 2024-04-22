plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:handler-client"))
  implementation(project(":lib:plugin-mapping"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.slf4j:slf4j-api")
}