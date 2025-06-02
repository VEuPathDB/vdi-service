plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:external-s3"))

  implementation(libs.s34k)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)

  implementation(common.config)
  implementation(common.logging)
  implementation(common.model)
}
