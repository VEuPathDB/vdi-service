import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  testImplementation("com.fasterxml.jackson.core:jackson-core:2.15.1")
  testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
  testImplementation("io.rest-assured:rest-assured:5.3.0")
  testImplementation("io.rest-assured:json-path:5.3.0")
  testImplementation("org.slf4j:slf4j-api:1.7.36")
}

tasks.withType<Test> {
  systemProperty("AUTH_TOKEN", project.property("AUTH_TOKEN")!!)
  systemProperty("BASE_URL", project.property("BASE_URL")!!)
  useJUnitPlatform()
  testLogging {
    events = setOf(TestLogEvent.STANDARD_OUT, TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
  }
}

repositories {
  mavenCentral()
}
