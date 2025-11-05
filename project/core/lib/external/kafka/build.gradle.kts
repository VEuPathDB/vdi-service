plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(common.json)
  implementation(common.model)
  implementation(common.util)

  implementation(libs.log.slf4j.api)
  implementation(libs.kt.coroutines)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
