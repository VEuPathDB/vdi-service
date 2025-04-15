plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:commons"))

  implementation(libs.vdi.common)
}
