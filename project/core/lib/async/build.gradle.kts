plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))

  api(libs.kt.coroutines)
  implementation(libs.log.log4j.kotlin)
}
