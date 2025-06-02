plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))

  implementation(libs.json.schema.validation)

  implementation(common.config)
  implementation(common.model)
}
