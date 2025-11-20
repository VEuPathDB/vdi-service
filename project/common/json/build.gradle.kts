plugins { id("build-conventions") }

dependencies {
  api(platform(libs.json.jackson.bom))
  api(libs.bundles.jackson)
}
