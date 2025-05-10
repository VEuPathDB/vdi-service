plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:env"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:pruner"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.common)

  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
  implementation(kotlin("stdlib-jdk8"))
}
