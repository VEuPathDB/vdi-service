plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:common"))
  implementation(project(":components:env"))
  implementation(project(":components:kafka"))
  implementation(project(":components:json"))
}