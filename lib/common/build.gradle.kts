plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.vdi.common)
  api(libs.prometheus.client)
  api(libs.prometheus.common)
  api(libs.log.slf4j)

  implementation(libs.deque)
}
