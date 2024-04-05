plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:app-db"))
  implementation(project(":components:env"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:metrics"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

  implementation("org.slf4j:slf4j-api")
}
