plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.json.schema.validation)

  implementation(common.model)
}
