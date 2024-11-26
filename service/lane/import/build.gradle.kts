plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:plugin-mapping"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.s34k)

  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
  implementation(libs.log.log4j.kotlin)
  implementation(kotlin("stdlib-jdk8"))
}
