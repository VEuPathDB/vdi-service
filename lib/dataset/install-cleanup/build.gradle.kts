plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:plugin:client"))

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)
}
