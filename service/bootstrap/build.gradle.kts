plugins {
  kotlin("jvm")
}

dependencies {
  implementation(libs.vdi.common)

  implementation(project(":lib:module-core"))
  implementation(project(":service:rest-service"))
  implementation(project(":service:daemon:event-router"))
  implementation(project(":service:daemon:reconciler"))
  implementation(project(":service:daemon:pruner"))
  implementation(project(":service:lane:hard-delete"))
  implementation(project(":service:lane:import"))
  implementation(project(":service:lane:install"))
  implementation(project(":service:lane:reconciliation"))
  implementation(project(":service:lane:sharing"))
  implementation(project(":service:lane:soft-delete"))
  implementation(project(":service:lane:update-meta"))

  implementation(libs.kt.coroutines)
  implementation(libs.container.core)

  implementation(libs.log.slf4j)
  implementation(libs.log.log4j.api)
  implementation(libs.log.log4j.core)
  implementation(libs.log.log4j.slf4j)
  implementation(kotlin("stdlib-jdk8"))
}
