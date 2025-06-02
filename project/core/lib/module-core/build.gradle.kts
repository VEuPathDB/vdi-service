plugins {
  id("vdi.conventions")
}

dependencies {
  api(project(":lib:async"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:external-s3"))

  implementation(libs.s34k)
  implementation(common.json)
  implementation(libs.log.slf4j.api)
}
