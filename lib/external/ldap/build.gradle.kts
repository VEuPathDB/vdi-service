plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))

  api(libs.ldap)

  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)

  testImplementation(kotlin("test"))
}
