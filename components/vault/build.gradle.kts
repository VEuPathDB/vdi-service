plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":components:constants"))

  implementation("io.github.jopenlibs:vault-java-driver")

  implementation("org.slf4j:slf4j-api")
}
