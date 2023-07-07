plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  api("org.veupathdb.lib:ldap-utils:1.0.0")

  implementation("org.slf4j:slf4j-api:1.7.36")

  testImplementation(kotlin("test"))
}
