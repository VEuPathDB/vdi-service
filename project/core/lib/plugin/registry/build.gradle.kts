plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))


  implementation(common.config)
}
