plugins { id("build-conventions") }

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-common"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))

  implementation(libs.s34k)
  implementation(libs.log.log4j.kotlin)
  implementation(libs.kt.coroutines)

  implementation(common.db.target)
  implementation(common.logging)
  implementation(common.util)

  testImplementation(kotlin("test"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.log.log4j.slf4j)
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}
