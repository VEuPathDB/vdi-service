plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:async"))
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.kt.coroutines)
  implementation(libs.vdi.common)
  implementation(libs.log.slf4j.api)
  implementation(libs.msg.rabbit)
}
