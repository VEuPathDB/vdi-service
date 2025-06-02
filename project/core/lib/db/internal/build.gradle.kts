plugins {
  id("build-conventions")
}

dependencies {
  api(project(":lib:db-common"))

  implementation(project(":lib:common"))

  implementation(common.config)
  implementation(common.model)
  implementation(common.logging)
  implementation(common.util)

  implementation(libs.db.pool)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
}
