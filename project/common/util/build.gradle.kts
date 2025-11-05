plugins { id("build-conventions") }

dependencies {
  implementation(project(":model"))

  implementation(libs.log.slf4j.api)
  implementation(libs.kt.coroutines)
  implementation(libs.compression)
}