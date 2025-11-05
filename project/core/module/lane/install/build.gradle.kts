import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("build-conventions")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:plugin-client"))
  implementation(project(":lib:external-kafka"))
  implementation(project(":lib:module-core"))
  implementation(project(":lib:external-s3"))

  implementation(common.logging)
  implementation(common.model)
  implementation(common.stack.io)

  implementation(libs.s34k)
  implementation(libs.kt.coroutines)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}