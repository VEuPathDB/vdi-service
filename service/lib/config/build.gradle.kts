plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.vdi.common)
  api(libs.vdi.json)
  implementation(libs.yaml)
  implementation(libs.json.schema.validation)
}
