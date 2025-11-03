plugins {
  id("build-conventions")
}

dependencies {
  api(project(":lib:async"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:external-s3"))

  implementation(libs.s34k)
  implementation(libs.log.slf4j.api)

  implementation(common.json)
  implementation(common.util)
}
