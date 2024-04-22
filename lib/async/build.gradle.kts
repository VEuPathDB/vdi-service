plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")
}
