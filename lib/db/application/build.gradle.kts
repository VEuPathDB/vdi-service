plugins {
  kotlin("jvm")
}

dependencies {

  implementation(project(":lib:common"))
  implementation(project(":lib:db:common"))
  implementation(project(":lib:external:ldap"))

  implementation(libs.vdi.common)

  implementation(libs.db.pool)
  implementation(libs.db.driver.oracle)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)

  implementation(libs.log.slf4j)
}
