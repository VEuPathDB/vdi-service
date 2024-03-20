import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:kafka"))
  implementation(project(":components:metrics"))
  implementation(project(":components:module-core"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:s3"))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")
  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.slf4j:slf4j-api")
  implementation(kotlin("stdlib-jdk8"))

  testImplementation(kotlin("test"))
  testImplementation(project(":components:test-utils"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
  }
}
