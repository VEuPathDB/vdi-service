plugins {
  id("vdi.conventions")
}

dependencies {
  subprojects.forEach { implementation(it) }
}
