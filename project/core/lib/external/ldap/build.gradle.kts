plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))

  api(libs.ldap)

  implementation(common.config)
  implementation(libs.log.slf4j.api)

  testImplementation(kotlin("test"))
}
