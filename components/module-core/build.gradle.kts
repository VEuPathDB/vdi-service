plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:kafka"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.veupathdb.vdi:vdi-component-json")

  implementation("org.slf4j:slf4j-api")
}