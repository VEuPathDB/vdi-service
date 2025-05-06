plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:plugin:registry"))

  implementation(libs.vdi.common)
  implementation(libs.vdi.json)
  implementation(libs.log.slf4j)
  implementation(libs.kt.coroutines)
  implementation(libs.http.client.multipart)
}
