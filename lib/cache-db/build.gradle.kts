plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:env"))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.slf4j:slf4j-api")

  implementation("com.zaxxer:HikariCP")
  implementation("org.postgresql:postgresql")
}
