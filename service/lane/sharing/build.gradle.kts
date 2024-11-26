plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:plugin-mapping"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:s3"))
  implementation(project(":lib:metrics"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
  implementation(kotlin("stdlib-jdk8"))
}
