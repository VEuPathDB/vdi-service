import org.veupathdb.lib.gradle.container.tasks.jaxrs.GenerateJaxRS
import org.veupathdb.lib.gradle.container.util.Logger

plugins {
  kotlin("jvm")
  alias(libs.plugins.vpdb)
}

containerService {
  logLevel = Logger.Level.Trace

  service {
    name = "vdi-service"
    projectPackage = "org.veupathdb.service.vdi"
  }

  raml {
    schemaRootDir = file("schema/types")
    rootApiDefinition = file("api-deprecation.raml")
    generateModelStreams = false
  }

  docker {
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
  implementation(project(":lib:plugins"))
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

  implementation(libs.kt.coroutines)

  implementation(libs.http.server.jersey)

  implementation(libs.log.slf4j)
  implementation(libs.log.log4j.core)
  implementation(libs.log.log4j.slf4j)

  implementation(libs.prometheus.client)
  implementation(libs.prometheus.common)

  testImplementation(kotlin("test"))
  testImplementation(libs.junit.api)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
  testRuntimeOnly(libs.junit.engine)
}
