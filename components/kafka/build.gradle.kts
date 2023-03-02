plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:common"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

  implementation("org.slf4j:slf4j-api:1.7.36")

  implementation("com.rabbitmq:amqp-client:5.16.0")
}
