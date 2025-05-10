plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)
  api(libs.kt.coroutines)
  implementation(libs.log.log4j.kotlin)
}
