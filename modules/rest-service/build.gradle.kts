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
}

dependencies {
  implementation("org.veupathdb.vdi:vdi-component-json:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-app-db:1.1.0")
  implementation("org.veupathdb.vdi:vdi-component-cache-db:1.0.0")
  implementation("org.veupathdb.vdi:vdi-component-common:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-plugin-mapping:1.0.0-SNAPSHOT") { isChanging = true }
  implementation("org.veupathdb.vdi:vdi-component-s3:1.2.0-SNAPSHOT") { isChanging = true }

  implementation("org.gusdb:fgputil-db:2.12.9")
  api("org.veupathdb.lib:jaxrs-container-core:6.14.3")
  implementation("org.veupathdb.lib:multipart-jackson-pojo:1.1.0")

  implementation("org.veupathdb.lib.s3:s34k-minio:0.3.6+s34k-0.7.2")

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.1.1")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

  // Log4J
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.20.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  implementation(kotlin("stdlib-jdk8"))
}
