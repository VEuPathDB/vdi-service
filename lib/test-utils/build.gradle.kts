plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.mockito:mockito-core:5.2.0")
  implementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:s3"))
}