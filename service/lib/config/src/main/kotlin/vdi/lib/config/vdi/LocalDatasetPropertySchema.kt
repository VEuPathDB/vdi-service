package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.json.JSON
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.exists

@JvmInline
internal value class LocalDatasetPropertySchema(private val path: Path): DatasetPropertySchema {
  private inline val logger get() = LoggerFactory.getLogger(DatasetPropertySchema::class.java)

  override fun resolve() = path.takeIf { it.exists() }
    ?.toFile()
    ?.also { logger.info("'$it' found in filesystem, loading schema") }
    ?.let { JSON.readTree(it) as ObjectNode }
    ?: path.takeIf { it.isAbsolute }
      ?.also { logger.info("'$it' not found in filesystem, attempting to find schema as jar-resource") }
      ?.let { DatasetPropertySchema::class.java.getResourceAsStream(it.toString()) }
      ?.use { JSON.readTree(it) as ObjectNode }
    ?: throw FileNotFoundException("target schema file '$path' not found in filesystem or jar resources")
}
