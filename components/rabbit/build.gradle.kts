plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("com.rabbitmq:amqp-client:5.17.0")
}
