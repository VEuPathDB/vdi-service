plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")
}
