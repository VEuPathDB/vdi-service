plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-handler-client:1.0.1")
  implementation("org.veupathdb.vdi:vdi-component-cache-db:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-s3:1.2.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-kafka:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-plugin-mapping:1.0.0-SNAPSHOT") { isChanging = true }

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.slf4j:slf4j-api:1.7.36")
}
