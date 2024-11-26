plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)
  implementation(libs.vdi.common)

  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:s3"))
}
