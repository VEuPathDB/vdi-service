plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)
  api(libs.ldap)
  implementation(libs.log.slf4j)

  testImplementation(kotlin("test"))
}
