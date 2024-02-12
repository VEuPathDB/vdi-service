plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.mockito:mockito-core:5.2.0")
  implementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:kafka"))
  implementation(project(":components:s3"))
}