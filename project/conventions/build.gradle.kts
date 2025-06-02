plugins { `kotlin-dsl` }

group = "vdi"
version = "1.0"

repositories {
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation(plugin(libs.plugins.kotlin))
}

kotlin {
  jvmToolchain(21)
}

fun plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }