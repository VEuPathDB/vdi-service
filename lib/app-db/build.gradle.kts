plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":lib:ldap"))

  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("com.zaxxer:HikariCP")
  implementation("com.oracle.database.jdbc:ojdbc8")
  implementation("org.postgresql:postgresql")

  implementation("org.slf4j:slf4j-api")
}
