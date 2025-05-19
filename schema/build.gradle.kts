import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.networknt.schema.ExecutionContext
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.yaml.snakeyaml.Yaml

plugins {
  `java-library`
}

val jsonSchemaBuildDir = layout.buildDirectory.dir("json-schema").get().asFile

sourceSets.main {
  resources.srcDir(jsonSchemaBuildDir)
}

tasks.processResources { dependsOn("build-config-schema-resource", "build-dataset-schema-resources") }

tasks.clean { delete(jsonSchemaBuildDir) }

tasks.register("build-config-schema-resource") {
  val json = ObjectMapper()

  val inputDir = file("config/")

  val outputFile = jsonSchemaBuildDir.resolve("schema/config/full-config.json")
  val rootSchema = inputDir.resolve("stack-config.json")

  val refRegex = Regex("\"\\\$ref\":\\s*\"([^\"]+)\"")

  inputs.dir(inputDir)
  outputs.file(outputFile)

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

  fun processResult(thisRefKey: String, path: File, res: Pair<String, Set<IntRange>>, buffer: StringBuilder, rootDefs: ObjectNode): ObjectNode {
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

  fun processResult(path: File, res: Pair<String, Set<IntRange>>, fixedJsonString: StringBuilder, rootDefs: ObjectNode) {
    val thisRefKey = path.relativeTo(rootSchema.parentFile).toPath().joinToString(";")
    rootDefs.set<ObjectNode>(thisRefKey, processResult(thisRefKey, path, res, fixedJsonString, rootDefs)
      .apply {
      remove("\$defs")
      remove("\$schema")
    })
  }
  doLast {
    val cache = HashMap<File, Pair<String, Set<IntRange>>>(32)
    val buffer = StringBuilder(16384)

    rootSchema.resolve(cache)

    val rootDefs = json.createObjectNode()!!
    val rootJson = processResult("", rootSchema, cache.remove(rootSchema)!!, buffer, rootDefs)

    if (rootJson.has("\$defs"))
      (rootJson.get("\$defs") as ObjectNode).setAll<ObjectNode>(rootDefs)
    else
      rootJson.set<ObjectNode>("\$defs", rootDefs)

    cache.forEach { (path, res) -> processResult(path, res, buffer, rootDefs) }

    json.writeValue(outputFile, rootJson)
  }
}

tasks.register("build-dataset-schema-resources") {
  val outputDir = jsonSchemaBuildDir.resolve("schema/data/")
  val schemaSourceDir = file("data/")

  inputs.dir(schemaSourceDir)
  outputs.dir(outputDir)

  doFirst {
    val json = ObjectMapper()

    val metaSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
      .getSchema(schemaSourceDir.resolve("dataset-characteristics.metaschema.json").toURI())

    outputDir.mkdirs()

    schemaSourceDir
      .listFiles()!!
      .onEach {
        if (it.name.endsWith("json"))
          it.copyTo(outputDir.resolve(it.name), true)
      }
      .filter { it.name.endsWith("yml") }
      .forEach {
        val content = json.convertValue(Yaml().loadAs(it.readText(), Any::class.java), ObjectNode::class.java)
          .apply { remove("\$schema") }

        metaSchema.validate(content) { ctx: ExecutionContext ->
          ctx.executionConfig.formatAssertionsEnabled = true
        }
          .takeUnless { it.isEmpty() }
          ?.also {
            System.err.println(json.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            throw IllegalStateException("invalid dataset property schema")
          }

        json.writeValue(outputDir.resolve(it.name.substringBeforeLast('.') + ".json"), content)
      }
  }
}
