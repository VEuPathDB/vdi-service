plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:handler-client"))
  implementation(project(":components:module-core"))
  implementation(project(":components:plugin-mapping"))

  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-cache-db:1.1.0")
  implementation("org.veupathdb.vdi:vdi-component-app-db:1.1.0")
  implementation("org.veupathdb.vdi:vdi-component-s3:1.2.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-kafka:1.0.0-SNAPSHOT") { isChanging = true }

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation(kotlin("stdlib-jdk8"))
}
