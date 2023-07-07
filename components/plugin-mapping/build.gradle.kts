plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:handler-client"))

  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}
