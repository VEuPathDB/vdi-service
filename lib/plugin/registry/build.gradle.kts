plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))

  implementation(libs.vdi.common)
}
