package vdi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

object ConfigSchemaCompiler {
  private lateinit var outputFile: File
  private lateinit var rootSchema: Path
  private lateinit var refRegex: Regex
  private lateinit var json: ObjectMapper

  fun init(task: Task, project: Project) {
    val inputDir = project.file("config/")

    rootSchema = inputDir.resolve("stack-config.json").toPath()
    outputFile = project.layout.buildDirectory.asFile.get().resolve("json-schema/schema/config/full-config.json")

    task.inputs.dir(inputDir)
    task.outputs.file(outputFile)
  }

  fun run() {
    refRegex = Regex("\"\\\$ref\":\\s*\"([^\"]+)\"")
    json = ObjectMapper()

    val cache = HashMap<Path, LoadResult>(32)
    val buffer = StringBuilder(16384) // 16KiB

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


  private fun Path.load(): LoadResult {
    val raw = readText()
    return LoadResult(
      raw,
      refRegex.findAll(raw, 0)
        .mapNotNull { it.groups[1] }
        .map { it.range }
        .filterNot { raw.substring(it.first..it.first + 3) == "http" }
        .toSet(),
    )
  }

  private fun Path.resolve(cache: MutableMap<Path, LoadResult>) {
    val result = load()

    cache[this] = result

    result.refs.asSequence()
      .map { result.raw.substring(it) }
      .filterNot { it.startsWith("http", 0, false) }
      .map { it.substringBefore('#').takeIf { p -> p.isNotBlank() } ?: this.toString() }
      .map { resolveSibling(it).normalize() }
      .filterNot { it in cache }
      .forEach { it.resolve(cache) }
  }

  private fun processResult(thisRefKey: String, path: Path, res: LoadResult, buffer: StringBuilder, rootDefs: ObjectNode): ObjectNode {
    var lastPos = 0

    for (range in res.refs) {
      val refKey: String

      if (res.raw[range.first] == '#') {
        refKey = thisRefKey + ";" + res.raw.substring(range).substringAfter("\$defs/")
      } else {
        val ref = res.raw.substring(range)
        refKey = rootSchema.parent.relativize(path.resolveSibling(ref.substringBefore('#'))).joinToString(";") +
        (ref.substringAfter('#', "").substringAfter("\$defs/").takeUnless { it.isBlank() }?.let { ";$it" } ?: "")
      }

      buffer.append(res.raw, lastPos, range.first)
        .append("#/\$defs/")
        .append(refKey)

      lastPos = range.last + 1
    }

    buffer.append(res.raw, lastPos, res.raw.length)

    val parsed = json.readTree(buffer.toString()) as ObjectNode
    buffer.clear()

    parsed.get("\$defs")
      ?.let { it as ObjectNode }
      ?.also { it.properties().forEach { (name, value) -> rootDefs.set<ObjectNode>("$thisRefKey;$name", value) } }

    parsed.remove("\$id")

    return parsed
  }

  private fun processResult(path: Path, res: LoadResult, fixedJsonString: StringBuilder, rootDefs: ObjectNode) {
    val thisRefKey = rootSchema.parent.relativize(path).joinToString(";")
    rootDefs.set<ObjectNode>(thisRefKey, processResult(thisRefKey, path, res, fixedJsonString, rootDefs).apply {
      remove("\$defs")
      remove("\$schema")
    })
  }

  private data class LoadResult(val raw: String, val refs: Set<IntRange>)
}
