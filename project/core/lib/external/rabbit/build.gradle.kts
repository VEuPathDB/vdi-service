plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:async"))
  implementation(project(":lib:common"))

  implementation(libs.kt.coroutines)
  implementation(libs.msg.rabbit)

  implementation(common.config)
  implementation(common.model)
  implementation(common.logging)
}
