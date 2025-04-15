plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:commons"))
  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:external:s3"))

  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.log.slf4j)
}
