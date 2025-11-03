import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins { `kotlin-dsl` }

repositories {
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation(plugin(libs.plugins.kotlin))
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

kotlin {
  compilerOptions.jvmTarget = JvmTarget.JVM_21
}

fun plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }