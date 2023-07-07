plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0")

  implementation("org.slf4j:slf4j-api:1.7.36")

  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("org.postgresql:postgresql:42.5.4")
}
