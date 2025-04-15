plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:commons"))
  implementation(project(":lib:db:commons"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.log.slf4j)

  implementation(libs.db.pool)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
}
