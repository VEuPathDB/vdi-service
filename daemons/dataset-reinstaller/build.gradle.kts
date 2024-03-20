plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:dataset-reinstaller"))
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.slf4j:slf4j-api")
}
