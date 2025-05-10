plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":lib:async"))
  implementation(project(":lib:external:kafka"))
  implementation(project(":lib:external:s3"))

  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.vdi.json)
  implementation(libs.log.slf4j)
}
