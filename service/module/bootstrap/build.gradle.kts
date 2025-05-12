plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)

  implementation(project(":lib:config"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:module-core"))
  implementation(project(":module:await-dependencies"))
  implementation(project(":module:rest-service"))
  implementation(project(":module:daemon:event-router"))
  implementation(project(":module:daemon:reconciler"))
  implementation(project(":module:daemon:pruner"))
  implementation(project(":module:lane:hard-delete"))
  implementation(project(":module:lane:import"))
  implementation(project(":module:lane:install"))
  implementation(project(":module:lane:reconciliation"))
  implementation(project(":module:lane:sharing"))
  implementation(project(":module:lane:soft-delete"))
  implementation(project(":module:lane:update-meta"))

  implementation(libs.kt.coroutines)
  implementation(libs.container.core)

  implementation(libs.log.slf4j.api)
  implementation(libs.log.slf4j.jcl)
  implementation(libs.log.slf4j.jul)
  implementation(libs.log.log4j.api)
  implementation(libs.log.log4j.core)
  implementation(libs.log.log4j.slf4j)
  implementation(kotlin("stdlib-jdk8"))
}
