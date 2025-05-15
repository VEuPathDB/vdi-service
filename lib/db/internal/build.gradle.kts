plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":lib:db:common"))

  implementation(project(":lib:common"))
  implementation(project(":lib:config"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.log.slf4j.api)

  implementation(libs.db.pool)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
}
