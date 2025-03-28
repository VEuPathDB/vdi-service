plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.log.slf4j)
}
