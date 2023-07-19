plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  implementation(project(":components:module-core"))
  implementation(project(":components:s3"))
  implementation(project(":components:kafka"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:app-db"))
  implementation(project(":components:handler-client"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:metrics"))

  api("org.veupathdb.lib.s3:s34k-minio:0.4.0+s34k-0.8.0")

  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test {
  useJUnitPlatform()
}
