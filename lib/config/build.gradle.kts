plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":schema"))

  api(libs.vdi.common)
  api(libs.vdi.json)

  implementation(libs.json.schema.validation)
  implementation(libs.log.slf4j.api)
  implementation(libs.yaml)
}
