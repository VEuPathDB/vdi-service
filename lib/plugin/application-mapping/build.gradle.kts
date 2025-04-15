plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:commons"))
  implementation(project(":lib:plugin:client"))
  implementation(project(":lib:plugin:registry"))

  implementation(libs.vdi.common)

  testImplementation(kotlin("test"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testRuntimeOnly(libs.junit.engine)
}
