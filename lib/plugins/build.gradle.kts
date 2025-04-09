plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:env"))

  implementation(libs.vdi.common)
}
