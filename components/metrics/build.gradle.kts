plugins {
  kotlin("jvm")
}

dependencies {
  api("io.prometheus:simpleclient:0.14.1")
  api("io.prometheus:simpleclient_common:0.14.1")

  implementation("org.slf4j:slf4j-api:1.7.36")
}
