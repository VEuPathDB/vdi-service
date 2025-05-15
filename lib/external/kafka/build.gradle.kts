plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.log.slf4j.api)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
