plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.mockito:mockito-core")
  implementation("org.mockito.kotlin:mockito-kotlin")

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:s3"))
}
