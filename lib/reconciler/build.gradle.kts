plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:plugin-mapping"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.log.log4j.kotlin)
  implementation(libs.kt.coroutines)

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
