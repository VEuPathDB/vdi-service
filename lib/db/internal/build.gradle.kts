plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:db:common"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.log.slf4j)

  implementation(libs.db.pool)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
}
