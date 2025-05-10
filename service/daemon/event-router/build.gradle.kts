plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:env"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:rabbit"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)

  implementation(kotlin("stdlib-jdk8"))
}
