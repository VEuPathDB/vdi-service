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
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(24)
  }
}

fun plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }