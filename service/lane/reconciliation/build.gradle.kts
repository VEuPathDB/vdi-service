import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:env"))
  implementation(project(":lib:kafka"))
  implementation(project(":lib:metrics"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:plugin-mapping"))
  implementation(project(":lib:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)
  implementation(libs.s34k)
  implementation(libs.kt.coroutines)
  implementation(libs.log.slf4j)
  implementation(kotlin("stdlib-jdk8"))

  testImplementation(kotlin("test"))
  testImplementation(project(":lib:test-utils"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.log.log4j.slf4j)
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
  }
}
