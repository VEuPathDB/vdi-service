plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:plugin:client"))
  implementation(project(":lib:plugin:application-mapping"))

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)
}
