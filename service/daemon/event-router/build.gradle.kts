plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:env"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:rabbit"))
  implementation(project(":lib:s3"))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.slf4j:slf4j-api")

  implementation(kotlin("stdlib-jdk8"))
}
