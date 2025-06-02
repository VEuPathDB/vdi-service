plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))

  implementation(common.config)
  implementation(common.json)
  implementation(common.model)

  implementation(libs.s34k)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j.api)
}
