plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.slf4j:slf4j-api")
}