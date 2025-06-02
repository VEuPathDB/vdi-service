plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-common"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))

  implementation(common.config)
  implementation(common.model)
  implementation(common.logging)

  implementation(libs.s34k)
  implementation(libs.kt.coroutines)

  testImplementation(kotlin("test"))
  testImplementation(project(":lib:test-utils"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.log.log4j.slf4j)
}
