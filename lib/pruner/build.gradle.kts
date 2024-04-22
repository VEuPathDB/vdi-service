plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.slf4j:slf4j-api")
}