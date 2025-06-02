plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:plugin-registry"))


  implementation(common.config)
  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)

  implementation(libs.log.slf4j.api)
  implementation(libs.kt.coroutines)
  implementation(libs.http.client.multipart)
}
