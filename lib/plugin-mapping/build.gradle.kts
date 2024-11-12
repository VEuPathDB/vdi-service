plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:env"))
  implementation(project(":lib:plugin-client"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.mockito:mockito-core")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
