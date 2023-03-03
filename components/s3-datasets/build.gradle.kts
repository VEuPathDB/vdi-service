plugins {
  kotlin("jvm") version "1.8.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(18))
  }
}

dependencies {
  implementation(project(":components:common"))
  implementation(project(":components:json"))

  implementation("org.veupathdb.lib.s3:s34k-minio:0.4.0+s34k-0.8.0")

  implementation("org.slf4j:slf4j-api:1.7.36")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
  useJUnitPlatform()
}