plugins { id("build-conventions") }

dependencies {
  api(project(":model"))

  implementation(project(":config"))

  implementation(platform(libs.json.jackson.bom))
  implementation(libs.bundles.jackson)
}