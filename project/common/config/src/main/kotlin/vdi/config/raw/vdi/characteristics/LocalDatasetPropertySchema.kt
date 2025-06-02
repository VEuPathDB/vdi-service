package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.json.JSON
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.exists
import vdi.logging.MetaLogger

@JvmInline
internal value class LocalDatasetPropertySchema(private val path: Path): DatasetPropertySchema {
  override fun resolve() = path.takeIf { it.exists() }
    ?.toFile()
    ?.also { MetaLogger.info("loading '$it' from filesystem") }
    ?.let { JSON.readTree(it) as ObjectNode }
    ?: path.takeIf { it.isAbsolute }
      ?.let { DatasetPropertySchema::class.java.getResourceAsStream(it.toString()) }
      ?.also { MetaLogger.info("loading '$path' from jar resources") }
      ?.use { JSON.readTree(it) as ObjectNode }
    ?: throw FileNotFoundException("data schema file '$path' not found in filesystem or jar resources")
}
