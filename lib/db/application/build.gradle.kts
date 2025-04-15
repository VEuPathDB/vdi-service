plugins {
  kotlin("jvm")
}

dependencies {

  implementation(project(":lib:commons"))
  implementation(project(":lib:db:commons"))
  implementation(project(":lib:external:ldap"))

  implementation(libs.vdi.common)

  implementation(libs.db.pool)
  implementation(libs.db.driver.oracle)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)

  implementation(libs.log.slf4j)
}
