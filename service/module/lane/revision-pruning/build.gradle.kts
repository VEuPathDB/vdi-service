plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:external:kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.s34k)
  implementation(libs.log.slf4j)
  implementation(libs.kt.coroutines)
}
