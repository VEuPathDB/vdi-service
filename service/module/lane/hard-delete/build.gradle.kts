plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:external:kafka"))
  implementation(project(":lib:module-core"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.log.slf4j)
  implementation(libs.kt.coroutines)
}
