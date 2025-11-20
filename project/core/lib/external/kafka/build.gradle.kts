plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)
  implementation(common.util)

  implementation(libs.kt.coroutines)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
