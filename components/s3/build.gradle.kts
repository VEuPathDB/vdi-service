plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  api("org.veupathdb.lib.s3:s34k-minio:0.6.0+s34k-0.10.0")

  implementation("org.slf4j:slf4j-api:1.7.36")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testImplementation("org.hamcrest:hamcrest:2.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}
