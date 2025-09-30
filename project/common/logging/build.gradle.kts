plugins { id("build-conventions") }

dependencies {
  api(libs.log.slf4j.api)
  api(project(":model"))
}