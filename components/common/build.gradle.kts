plugins {
  kotlin("jvm") version "1.8.0"
}

kotlin {
  jvmToolchain(18)
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}