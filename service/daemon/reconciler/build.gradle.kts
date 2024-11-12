plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation(project(":lib:env"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:reconciler"))

  implementation("org.apache.logging.log4j:log4j-api-kotlin")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.mockito:mockito-core")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}

tasks.test {
  useJUnitPlatform()

  testLogging.showStandardStreams = true
}
