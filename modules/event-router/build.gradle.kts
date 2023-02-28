plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:json"))

  implementation("org.slf4j:slf4j-simple:2.0.6")
  implementation("org.apache.kafka:kafka-clients:3.4.0")
  implementation("org.apache.kafka:kafka-streams:3.4.0")
  implementation("com.rabbitmq:amqp-client:5.16.0")
}