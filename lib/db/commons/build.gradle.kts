plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}
