plugins {
  id("build-conventions")
}

dependencies {
  api(project(":lib:db-common"))

  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:plugin-registry"))

  implementation(common.db.target)
  implementation(common.model)
  implementation(common.json)
  implementation(common.util)

  implementation(libs.db.pool)
  implementation(libs.db.driver.oracle)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)

  implementation(libs.log.slf4j.api)
}
