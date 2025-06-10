plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))

  implementation(common.config)
  implementation(common.logging)

  implementation(libs.kt.coroutines)
}
