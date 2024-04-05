plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:env"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:kafka"))
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.apache.logging.log4j:log4j-api-kotlin")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}
