import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  kotlin("jvm")
  id("org.veupathdb.lib.gradle.container.container-utils") version "5.0.3"
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
  implementation(platform(project(":platform")))

  implementation(project(":components:app-db"))
  implementation(project(":components:cache-db"))
  implementation(project(":components:install-cleanup"))
  implementation(project(":components:plugin-mapping"))
  implementation(project(":components:pruner"))
  implementation(project(":components:s3"))
  implementation(project(":components:metrics"))

  implementation("org.veupathdb.vdi:vdi-component-json")
  implementation("org.veupathdb.vdi:vdi-component-common")

  implementation("org.gusdb:fgputil-db")
  implementation("org.veupathdb.lib:jaxrs-container-core")
  implementation("org.veupathdb.lib:multipart-jackson-pojo")

  implementation("org.veupathdb.lib.s3:s34k-minio")

  implementation(kotlin("stdlib-jdk8"))

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.core:jackson-annotations")

  // Log4J
  implementation("org.slf4j:slf4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")


  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation("io.prometheus:simpleclient")
  implementation("io.prometheus:simpleclient_common")

  // Unit Testing
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}
