plugins {
  kotlin("jvm")
}

dependencies {
  subprojects.forEach { implementation(it) }
}
