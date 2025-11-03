plugins {
  id("build-conventions")
}

dependencies {
  api(libs.ldap)
  implementation(project(":lib:config"))
}
