plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:dataset-pruner"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))


  implementation(libs.kt.coroutines)

  implementation(common.config)
  implementation(common.logging)
}
