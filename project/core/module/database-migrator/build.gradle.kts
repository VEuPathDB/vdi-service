plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:config"))

  implementation(common.config)
  implementation(common.logging)
  implementation(libs.db.driver.postgres)
  implementation(libs.db.kotlin.extended)
  implementation(libs.db.pool)
}
