plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-rabbit"))
  implementation(project(":lib:external-s3"))

  implementation(common.json)
  implementation(common.config)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)

  implementation(kotlin("stdlib-jdk8"))
}
