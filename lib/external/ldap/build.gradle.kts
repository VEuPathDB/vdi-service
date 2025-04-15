plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:commons"))

  api(libs.ldap)

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)

  testImplementation(kotlin("test"))
}
