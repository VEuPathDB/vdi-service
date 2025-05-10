plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:dataset:pruner"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external:s3"))

  implementation(libs.vdi.common)

  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
  implementation(kotlin("stdlib-jdk8"))
}
