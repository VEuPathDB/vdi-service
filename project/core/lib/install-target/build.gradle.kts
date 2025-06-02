plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))

  implementation(libs.json.schema.validation)

  implementation(common.config)
}
