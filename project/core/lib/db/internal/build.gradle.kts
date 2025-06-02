plugins {
  id("vdi.conventions")
}

dependencies {
  api(project(":lib:db-common"))

  implementation(project(":lib:common"))

  implementation(common.json)
  implementation(common.config)
  implementation(common.model)
  implementation(common.util)

  implementation(libs.log.slf4j.api)

  implementation(libs.db.pool)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
}
