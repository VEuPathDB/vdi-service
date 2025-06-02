plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))


  implementation(common.config)
  implementation(common.model)
}
