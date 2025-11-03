plugins { id("build-conventions") }

dependencies {
  api(common.config)
  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)
}
