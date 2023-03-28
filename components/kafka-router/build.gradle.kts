plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  implementation(project(":components:common"))
  implementation(project(":components:kafka"))
  implementation(project(":components:kafka-triggers"))

//  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.kafka:kafka-clients:3.4.0")
//  implementation("org.apache.kafka:kafka-streams:3.4.0")
}
