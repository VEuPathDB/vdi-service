plugins {
  id("build-conventions")
}

dependencies {
  api(libs.ldap)
  implementation(common.config)
}
