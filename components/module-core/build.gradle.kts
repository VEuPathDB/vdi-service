plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:kafka"))

  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-s3:1.2.0-SNAPSHOT") { isChanging = true }

  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")

  implementation("org.slf4j:slf4j-api:1.7.36")
}