plugins { id("build-conventions") }

dependencies {
  api(project(":model"))

  implementation(platform(libs.json.jackson.bom))
  implementation(libs.bundles.jackson)
}