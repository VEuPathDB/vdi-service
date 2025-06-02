plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))

  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)

  implementation(common.config)
}
