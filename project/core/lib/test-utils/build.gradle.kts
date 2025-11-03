plugins {
  id("build-conventions")
}

dependencies {
  implementation(common.model)

  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)

  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-common"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:external-s3"))
}
