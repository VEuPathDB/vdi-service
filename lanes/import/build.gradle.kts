plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:cache-db"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:kafka"))
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.slf4j:slf4j-api")
  implementation("org.apache.logging.log4j:log4j-api-kotlin")
  implementation(kotlin("stdlib-jdk8"))
}
