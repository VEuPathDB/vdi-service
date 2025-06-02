plugins {
  id("vdi.conventions")
}

dependencies {
  implementation(project(":lib:common"))

  implementation(common.config)
  implementation(common.json)
  implementation(common.model)
  implementation(libs.log.slf4j.api)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
