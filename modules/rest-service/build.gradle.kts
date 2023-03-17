import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  kotlin("jvm")
  id("org.veupathdb.lib.gradle.container.container-utils") version "4.8.3"
}

// configure VEupathDB container plugin
containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Info

  // General project level configuration.
  project {

    // Project Name
    name = "vdi-service"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.vdi"

    // Main Class Name
    mainClassName = "Main"
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"

    // Resulting image tag
    imageName = "vdi-service"

  }

  generateJaxRS {
    // List of custom arguments to use in the jax-rs code generation command
    // execution.
    arguments = listOf(/*arg1, arg2, arg3*/)

    // Map of custom environment variables to set for the jax-rs code generation
    // command execution.
    environment = mapOf(/*Pair("env-key", "env-val"), Pair("env-key", "env-val")*/)
  }

}

dependencies {
  implementation(project(":components:cache-db"))
  implementation(project(":components:common"))
  implementation(project(":components:compression"))
  implementation(project(":components:env"))
  implementation(project(":components:json"))
  implementation(project(":components:s3-datasets"))

  api("org.veupathdb.lib:jaxrs-container-core:6.14.3")
  implementation("org.veupathdb.lib:multipart-jackson-pojo:1.1.0")

  implementation("org.veupathdb.lib:ldap-utils:1.0.0")
  implementation("org.veupathdb.lib.s3:s34k-minio:0.3.6+s34k-0.7.2")

  // Database Connectivity
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.oracle.database.jdbc:ojdbc8:21.1.0.0")
  implementation("org.postgresql:postgresql:42.5.4")


  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.1.0")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.0")

  // Log4J
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.19.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}
