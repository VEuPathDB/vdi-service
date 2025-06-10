plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(common.json)
  implementation(common.model)
  implementation(libs.log.slf4j.api)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
