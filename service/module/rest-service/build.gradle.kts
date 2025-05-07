import org.veupathdb.lib.gradle.container.tasks.jaxrs.GenerateJaxRS
import org.veupathdb.lib.gradle.container.tasks.raml.GenerateRamlDocs

plugins {
  kotlin("jvm")
  alias(libs.plugins.vpdb)
}

containerService {
  service {
    name = "vdi-service"
    projectPackage = "vdi.service.rest"
  }

  raml {
    schemaRootDir = file("api-schema/types")
    generateModelStreams = false
    mergeToolVersion = "v2.1.1"
  }

  docker {
    imageName = "vdi-service"
  }
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:dataset:pruner"))
  implementation(project(":lib:dataset:reconciler"))
  implementation(project(":lib:dataset:reinstaller"))
  implementation(project(":lib:db:application"))
  implementation(project(":lib:db:internal"))
  implementation(project(":lib:db:common"))
  implementation(project(":lib:external:s3"))
  implementation(project(":lib:install-target"))
  implementation(project(":lib:plugin:registry"))

  implementation(libs.vdi.json)
  implementation(libs.vdi.common)

  implementation(libs.fgputil.db)
  implementation(libs.container.core)
  implementation(libs.container.multipart)

  implementation(libs.json.request.validation)
  implementation(libs.json.schema.validation)

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

tasks.register("raml-docs") {
  dependsOn("merge-raml")

  inputs.sourceFiles.files.addAll(listOf(
    file("deprecated-api.raml"),
    file("api.raml"),
    file("api-schema/library.raml")
  ))
  outputs.files("docs/api.html", "src/main/resources/api.html")

  doFirst {
    val originalFile = containerService.raml.rootApiDefinition
    containerService.raml.rootApiDefinition = file("deprecated-api.raml")

    with(tasks.getByName<GenerateRamlDocs>(GenerateRamlDocs.TaskName)) {
      execute()
    }

    containerService.raml.rootApiDefinition = originalFile
  }
}

tasks.register("special-patches") {
  // Filename prefixes for files that should be patched.
  val targetPrefixes = arrayOf(
    "DatasetMetaBase",
    "DatasetPostMeta",
    "DatasetProxyPostMeta",
    "DatasetPatchRequest",
    "DatasetPutMetadata",
    "DatasetDetails",
  )

  doLast {
    val packagePath = containerService
      .service
      .projectPackage
      .replace('.', '/') + "/generated/model"

    val pattern = Regex("\\bObject\\b")

    sourceSets.asSequence()
      .map { it.allSource }
      .map { it.srcDirs }
      .flatMap { it.asSequence() }
      .map { it.resolve(packagePath) }
      .filter { it.exists() }
      .map { it.listFiles()!! }
      .flatMap { it.asSequence() }
      .filter { file -> targetPrefixes.any { file.name.startsWith(it) } }
      .forEach { it.writeText(it.readText().replace(pattern, "com.fasterxml.jackson.databind.node.ObjectNode")) }
  }
}

tasks.withType<GenerateJaxRS> { finalizedBy("special-patches") }

