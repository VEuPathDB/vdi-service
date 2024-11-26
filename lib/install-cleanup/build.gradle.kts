plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:plugin-mapping"))

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)
}
