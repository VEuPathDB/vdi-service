plugins {
  id("build-conventions")
}

dependencies {
  subprojects.forEach { implementation(it) }
}
