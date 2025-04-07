plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:env"))
  implementation(project(":lib:health"))
  implementation(project(":lib:jdbc"))
  implementation(project(":lib:ldap"))

  implementation(libs.vdi.common)

  implementation(libs.db.pool)
  implementation(libs.db.driver.oracle)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)

  implementation(libs.log.slf4j)
}
