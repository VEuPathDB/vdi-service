plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)
  implementation(libs.vdi.common)

  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:external:s3"))
}
