plugins {
  kotlin("jvm")
  alias(libs.plugins.vpdb)
}

containerService {
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
  implementation(project(":lib:common"))
  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:db:common"))
  implementation(project(":lib:dataset:reinstaller"))
  implementation(project(":lib:plugin:registry"))
  implementation(project(":lib:dataset:pruner"))
  implementation(project(":lib:dataset:reconciler"))
  implementation(project(":lib:external:s3"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.fgputil.db)
  implementation(libs.container.core)
  implementation(libs.container.multipart)

  implementation(libs.json.validation)

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
