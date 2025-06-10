plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-rabbit"))
  implementation(project(":lib:external-s3"))

  implementation(common.config)
  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)

  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)
}
