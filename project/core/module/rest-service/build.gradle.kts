import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.openapitools.generator.gradle.plugin.tasks.MetaTask
import org.veupathdb.lib.gradle.container.tasks.jaxrs.GenerateJaxRS
import org.veupathdb.lib.gradle.container.tasks.raml.GenerateRamlDocs

plugins {
  alias(libs.plugins.vpdb)
  id("build-conventions")
}

buildscript {
  repositories { mavenLocal(); mavenCentral() }
  dependencies {
    classpath("org.openapitools:openapi-generator-gradle-plugin:7.13.0")
    classpath("org.veupathdb.lib:oas-jaxrs-kt:1.0.0-SNAPSHOT")
    classpath("gg.jte:jte:3.2.1")
  }
}

apply { plugin("org.openapi.generator") }

tasks.withType<GenerateTask> {
  val newSrcPath = "src/generated/kotlin"
  val newResPath = "src/generated/resources"

  apiPackage = "vdi.service.rest.gen.api"
  modelPackage = "vdi.service.rest.gen.model"
  generatorName = "vpdb"

  inputSpec = "$projectDir/openapi.yml"
  outputDir = projectDir.path

  additionalProperties = mapOf(
    "dateLibrary" to "java8",
    "enumPropertyNaming" to "PascalCase",
    "sourceFolder" to newSrcPath,
    "resourceFolder" to newResPath,
    "debugModels" to "$projectDir/models.json",
    "debugOperations" to "$projectDir/operations.json",
  )

  nameMappings = mapOf(
    "data" to "datasetData",
    "meta" to "datasetMeta",
  )

  skipOperationExample = true

  ignoreFileOverride = "$projectDir/.openapi-generator-ignore"

  globalProperties = mapOf(
    "apis"        to "",
    "models"      to "",
    "verbose"     to "true",
  )
  inlineSchemaOptions = mapOf(
    "RESOLVE_INLINE_ENUMS" to "true",
    "REFACTOR_ALLOF_INLINE_SCHEMAS" to "true",
    "ADD_UNSIGNED_TO_INTEGER_WITH_INVALID_MAX_VALUE" to "true",
  )
  openapiNormalizer = mapOf(
    "REF_AS_PARENT_IN_ALLOF" to "true"
  )

  doFirst {
    file(newSrcPath).deleteRecursively()
    file(newResPath).deleteRecursively()
  }
}

tasks.withType<MetaTask> {
  outputFolder = "${rootDir}/derp"
  generatorName = "vpdb-jaxrs-kt"
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

sourceSets {
  main {
    kotlin {
      srcDir("src/generated/kotlin")
    }
  }
}

configurations.all {
  exclude(group = "commons-logging", module = "commons-logging")
}

dependencies {
  implementation(project(":lib:common"))
  implementation(project(":lib:config"))
  implementation(project(":lib:dataset-pruner"))
  implementation(project(":lib:dataset-reconciler"))
  implementation(project(":lib:dataset-reinstaller"))
  implementation(project(":lib:db-application"))
  implementation(project(":lib:db-internal"))
  implementation(project(":lib:db-common"))
  implementation(project(":lib:external-s3"))
  implementation(project(":lib:install-target"))
  implementation(project(":lib:plugin-registry"))

  implementation(common.json)
  implementation(common.logging)
  implementation(common.model)
  implementation(common.util)

  implementation(libs.fgputil.db)
  implementation(libs.container.core)

  implementation(libs.container.multipart)

  implementation(libs.json.request.validation)
  implementation(libs.json.schema.validation)

  implementation(libs.s34k)

  implementation(libs.kt.coroutines)

  implementation(libs.http.server.jersey)

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

