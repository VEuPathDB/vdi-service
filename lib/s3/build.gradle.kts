plugins {
  kotlin("jvm")
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}

dependencies {
  implementation(project(":lib:env"))
  implementation(project(":lib:health"))
  implementation(project(":lib:metrics"))

  implementation(libs.vdi.common)
  implementation(libs.vdi.json)
  implementation(libs.s34k)
  implementation(libs.log.slf4j)

  testImplementation(kotlin("test"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testImplementation("org.hamcrest:hamcrest:2.2")
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.log.log4j.slf4j)
}
