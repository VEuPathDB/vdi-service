import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

plugins { id("build-conventions") }

dependencies {
  implementation(libs.yaml)
  implementation(libs.json.schema.validation)

  implementation(project(":json"))
  implementation(project(":logging"))
  implementation(project(":model"))
}


val jsonSchemaBuildDir = findProperty("SCHEMA_BUILD_DIR")?.let { File(it as String) }
  ?: layout.buildDirectory.dir("json-schema").get().asFile

sourceSets.main {
  resources.srcDir(jsonSchemaBuildDir)
}

tasks.clean { delete(jsonSchemaBuildDir) }

val json = ObjectMapper()
val inputDir = file("src/main/json/schema/")
val refRegex = Regex("\"\\\$ref\":\\s*\"([^\"]+)\"")

fun File.load() =
  readText().let { raw ->
    raw to refRegex.findAll(raw, 0)
      .mapNotNull { it.groups[1] }
      .map { it.range }
      .filterNot { raw.substring(it.first..it.first+3) == "http" }
      .toSet()
  }

fun File.resolve(cache: MutableMap<File, Pair<String, Set<IntRange>>>) {
  load()
    .also { cache[this] = it }
    .also { (raw, refs) ->
      refs.asSequence()
        .map { raw.substring(it) }
        .filterNot { it.startsWith("http", 0, false) }
        .map { it.substringBefore('#').takeIf { p -> p.isNotBlank() } ?: this.path }
        .map { resolveSibling(it).normalize() }
        .filterNot { it in cache }
        .forEach { it.resolve(cache) }
    }
}

fun processResult(
  thisRefKey: String,
  path: File,
  res: Pair<String, Set<IntRange>>,
  buffer: StringBuilder,
  rootDefs: ObjectNode,
  rootSchema: File,
): ObjectNode {
  var lastPos = 0

  for (range in res.second) {
    val refKey: String

    if (res.first[range.first] == '#') {
      refKey = thisRefKey + ";" + res.first.substring(range).substringAfter("\$defs/")
    } else {
      val ref = res.first.substring(range)
      refKey = path.resolveSibling(ref.substringBefore('#')).relativeTo(rootSchema.parentFile).toPath().joinToString(";") +
      (ref.substringAfter('#', "").substringAfter("\$defs/").takeUnless { it.isBlank() }?.let { ";$it" } ?: "")
    }

    buffer.append(res.first, lastPos, range.first)
      .append("#/\$defs/")
      .append(refKey)

    lastPos = range.last + 1
  }

  buffer.append(res.first, lastPos, res.first.length)

  val parsed = json.readTree(buffer.toString()) as ObjectNode
  buffer.clear()

  parsed.get("\$defs")
    ?.let { it as ObjectNode }
    ?.also { it.fields().forEach { (name, value) -> rootDefs.set<ObjectNode>("$thisRefKey;$name", value) } }

  parsed.remove("\$id")

  return parsed
}

fun processResult(
  path: File,
  res: Pair<String, Set<IntRange>>,
  fixedJsonString: StringBuilder,
  rootDefs: ObjectNode,
  rootSchema: File,
) {
  val thisRefKey = path.relativeTo(rootSchema.parentFile).toPath().joinToString(";")
  rootDefs.set<ObjectNode>(thisRefKey, processResult(thisRefKey, path, res, fixedJsonString, rootDefs, rootSchema)
    .apply {
      remove("\$defs")
      remove("\$schema")
    })
}

fun buildMergedSchema(rootSchema: File): ObjectNode {
  val cache = HashMap<File, Pair<String, Set<IntRange>>>(32)
  val buffer = StringBuilder(16384)

  rootSchema.resolve(cache)

  val rootDefs = json.createObjectNode()!!
  val rootJson = processResult("", rootSchema, cache.remove(rootSchema)!!, buffer, rootDefs, rootSchema)

  if (rootJson.has("\$defs"))
    (rootJson.get("\$defs") as ObjectNode).setAll<ObjectNode>(rootDefs)
  else
    rootJson.set("\$defs", rootDefs)

  cache.forEach { (path, res) -> processResult(path, res, buffer, rootDefs, rootSchema) }

  return rootJson
}

val compDbConfig = tasks.register("compile-app-db-schema") {
  val outputFile = jsonSchemaBuildDir.resolve("schema/config/app-db-config.json")
  val rootSchema = inputDir.resolve("install-target-config.json")

  inputs.dir(inputDir)
  outputs.file(outputFile)

  doLast { json.writeValue(outputFile, buildMergedSchema(rootSchema)) }
}

val compPlugConfig = tasks.register("compile-plugin-schema") {
  val outputFile = jsonSchemaBuildDir.resolve("schema/config/plugin-config.json")
  val rootSchema = inputDir.resolve("plugin-config.json")

  inputs.dir(inputDir)
  outputs.file(outputFile)

  doLast { json.writeValue(outputFile, buildMergedSchema(rootSchema)) }
}

val compCoreConfig = tasks.register("compile-core-schema") {
  val outputFile = jsonSchemaBuildDir.resolve("schema/config/full-config.json")
  val rootSchema = inputDir.resolve("core-config.json")

  inputs.dir(inputDir)
  outputs.file(outputFile)

  doLast { json.writeValue(outputFile, buildMergedSchema(rootSchema)) }
}

val compConfig = tasks.register("compile-config-schema") {
  dependsOn(
    compPlugConfig,
    compCoreConfig,
    compDbConfig,
  )
}

tasks.processResources { dependsOn(compConfig) }