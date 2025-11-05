plugins { id("build-conventions") }

dependencies {
  api(project(":model"))

  implementation(libs.json.jackson.databind)
}