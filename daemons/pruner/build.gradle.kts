plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))
  implementation(project(":components:pruner"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.slf4j:slf4j-api")
  implementation(kotlin("stdlib-jdk8"))
}