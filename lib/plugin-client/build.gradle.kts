plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)
  implementation(libs.vdi.json)
  implementation(libs.log.slf4j)
  implementation(libs.kt.coroutines)
  implementation(libs.http.client.multipart)
}
