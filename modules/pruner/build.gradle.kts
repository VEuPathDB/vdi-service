plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))
  implementation(project(":components:pruner"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation(kotlin("stdlib-jdk8"))
}