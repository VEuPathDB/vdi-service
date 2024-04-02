plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.slf4j:slf4j-api")

  implementation("org.apache.kafka:kafka-clients")

  api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
