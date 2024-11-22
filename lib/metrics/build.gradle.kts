plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.prometheus.client)
  api(libs.prometheus.common)
  implementation(libs.log.slf4j)
}
