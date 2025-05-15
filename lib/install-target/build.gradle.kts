plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.vdi.common)
  implementation(libs.json.schema.validation)
}
