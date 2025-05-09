plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":lib:db:common"))

  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:external:ldap"))

  implementation(libs.vdi.common)
  implementation(libs.vdi.json)

  implementation(libs.db.pool)
  implementation(libs.db.driver.oracle)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)

  implementation(libs.log.slf4j)
}
