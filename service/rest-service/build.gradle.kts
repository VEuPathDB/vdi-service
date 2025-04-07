import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  kotlin("jvm")
  id("org.veupathdb.lib.gradle.container.container-utils") version "6.1.0"
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
  implementation(project(":lib:app-db"))
  implementation(project(":lib:cache-db"))
  implementation(project(":lib:dataset-reinstaller"))
  implementation(project(":lib:env"))
  implementation(project(":lib:health"))
  implementation(project(":lib:install-cleanup"))
  implementation(project(":lib:plugin-mapping"))
  implementation(project(":lib:pruner"))
  implementation(project(":lib:reconciler"))
  implementation(project(":lib:s3"))
  implementation(project(":lib:metrics"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.fgputil.db)
  implementation(libs.container.core)
  implementation(libs.container.multipart)

  implementation(libs.s34k)

  implementation(kotlin("stdlib-jdk8"))
  implementation(libs.kt.coroutines)

  // Jersey
  implementation(libs.http.server.jersey)

  // Log4J
  implementation(libs.log.slf4j)
  implementation(libs.log.log4j.core)
  implementation(libs.log.log4j.slf4j)

  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation(libs.prometheus.client)
  implementation(libs.prometheus.common)

  // Unit Testing
  testImplementation(kotlin("test"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
  testRuntimeOnly(libs.junit.engine)
}
