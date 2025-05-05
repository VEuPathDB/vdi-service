plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)

  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:dataset:reconciler"))

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
