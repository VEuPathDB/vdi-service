plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:dataset-reinstaller"))
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))

  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
  implementation("org.slf4j:slf4j-api:1.7.36")
}
