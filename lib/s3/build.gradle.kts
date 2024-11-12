plugins {
  kotlin("jvm")
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:env"))
  implementation(project(":lib:metrics"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.vdi:vdi-component-json")

  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.slf4j:slf4j-api")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.hamcrest:hamcrest:2.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}
