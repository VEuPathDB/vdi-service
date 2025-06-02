plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:dataset-pruner"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))


  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)
  implementation(kotlin("stdlib-jdk8"))

  implementation(common.config)
}
