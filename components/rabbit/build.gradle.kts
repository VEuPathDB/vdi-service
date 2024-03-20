plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))
  implementation(project(":components:async"))
  implementation(project(":components:metrics"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.slf4j:slf4j-api")
  implementation("com.rabbitmq:amqp-client")
}
