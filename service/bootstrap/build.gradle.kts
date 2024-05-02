plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation("org.veupathdb.vdi:vdi-component-common")

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

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.veupathdb.lib:jaxrs-container-core")

  implementation("org.slf4j:slf4j-api")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")
  implementation(kotlin("stdlib-jdk8"))
}
