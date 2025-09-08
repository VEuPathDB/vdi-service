plugins { id("build-conventions") }

dependencies {
  api(project(":model"))

  implementation(project(":config"))
  implementation(project(":logging"))

  implementation(libs.ldap)
  implementation(libs.json.jackson.databind)
  implementation(libs.json.schema.validation)
}