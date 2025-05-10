plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
}
