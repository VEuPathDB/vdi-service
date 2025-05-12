plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  api(libs.ldap)

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j.api)

  testImplementation(kotlin("test"))
}
