plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))

  implementation(common.json)
  implementation(common.config)

  implementation(libs.log.slf4j.api)
  implementation(libs.kt.coroutines)
}
