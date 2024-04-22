plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.vdi:vdi-component-json")

  implementation("org.slf4j:slf4j-api")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

  implementation("io.foxcapades.lib:k-multipart")
}
