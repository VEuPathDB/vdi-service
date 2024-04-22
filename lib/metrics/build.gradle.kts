plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  api("io.prometheus:simpleclient")
  api("io.prometheus:simpleclient_common")

  implementation("org.slf4j:slf4j-api")
}
