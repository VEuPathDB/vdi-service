plugins {
  kotlin("jvm")
}

tasks.test {
  useJUnitPlatform {
    excludeTags = mutableSetOf("ListAllDatasets")
  }

  testLogging.showStandardStreams = true
}

tasks.register<Test>("list-all-datasets") {
  useJUnitPlatform {
    includeTags = mutableSetOf("ListAllDatasets")
  }

  testLogging.showStandardStreams = true
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:metrics"))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.vdi:vdi-component-json")

  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.slf4j:slf4j-api")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testImplementation("org.hamcrest:hamcrest:2.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}
