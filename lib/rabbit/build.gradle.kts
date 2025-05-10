plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:async"))
  implementation(project(":lib:env"))
  implementation(project(":lib:health"))
  implementation(project(":lib:metrics"))

  implementation(libs.kt.coroutines)
  implementation(libs.vdi.common)
  implementation(libs.log.slf4j)
  implementation(libs.msg.rabbit)
}
