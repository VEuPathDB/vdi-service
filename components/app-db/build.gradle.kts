plugins {
  kotlin("jvm")
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-ldap:1.0.0")

  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.oracle.database.jdbc:ojdbc8:21.9.0.0")
  implementation("org.postgresql:postgresql:42.5.4")

  implementation("org.slf4j:slf4j-api:1.7.36")
}
