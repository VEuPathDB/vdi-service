plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.vdi.common)
  api(libs.vdi.json)
  implementation(libs.json.schema.validation)
  implementation(libs.log.slf4j)
  implementation(libs.yaml)
}
