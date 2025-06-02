plugins { id("build-conventions") }

dependencies {
  implementation(libs.yaml)
  implementation(libs.json.schema.validation)

  implementation(project(":json"))
  implementation(project(":logging"))
  implementation(project(":model"))
  implementation(common.schema)
}
