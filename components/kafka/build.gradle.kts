plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.kafka:kafka-clients:3.4.0")
  implementation("org.apache.kafka:kafka-streams:3.4.0")
}
