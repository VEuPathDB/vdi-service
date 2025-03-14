plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:env"))
  implementation(project(":lib:health"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)
  implementation(libs.msg.kafka)

  testImplementation(kotlin("test"))
}
