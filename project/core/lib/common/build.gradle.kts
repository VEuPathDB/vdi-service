plugins {
  id("vdi.conventions")
}

dependencies {
  api(libs.prometheus.client)
  api(libs.prometheus.common)
  api(libs.log.slf4j.api)

  implementation(libs.deque)
}
