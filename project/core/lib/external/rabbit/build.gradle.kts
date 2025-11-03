plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:async"))
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.kt.coroutines)
  implementation(libs.msg.rabbit)

  implementation(common.model)
  implementation(common.logging)
}
