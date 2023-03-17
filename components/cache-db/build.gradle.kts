plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":components:common"))

  implementation("org.slf4j:slf4j-api:1.7.36")

  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("org.postgresql:postgresql:42.5.4")
}