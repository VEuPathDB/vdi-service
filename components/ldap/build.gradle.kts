plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")

  api("org.veupathdb.lib:ldap-utils")

  implementation("org.slf4j:slf4j-api")

  testImplementation(kotlin("test"))
}
