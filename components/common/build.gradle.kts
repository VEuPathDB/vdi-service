plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.apache.commons:commons-compress:1.22")
  implementation("org.slf4j:slf4j-api:1.7.36")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}