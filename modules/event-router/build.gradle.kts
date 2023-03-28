plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  implementation(project(":components:common"))
  implementation(project(":components:env"))
  implementation(project(":components:s3-datasets"))
  implementation(project(":components:kafka"))
  implementation(project(":components:kafka-router"))
  implementation(project(":components:kafka-triggers"))
  implementation(project(":components:rabbit"))

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.slf4j:slf4j-api:1.7.36")
}