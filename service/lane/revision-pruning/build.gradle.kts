plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.s34k)
  implementation(libs.log.slf4j)
  implementation(libs.kt.coroutines)
}
