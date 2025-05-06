package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.vdi.lib.json.JSON
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.exists

@JvmInline
internal value class LocalDatasetPropertySchema(private val path: Path): DatasetPropertySchema {
  override fun resolve() =
    path.takeIf { it.exists() }
      ?.toFile()
      ?.let { JSON.readTree(it) as ObjectNode }
      ?: path.takeIf { it.isAbsolute }
        ?.let { it.javaClass.getResourceAsStream(it.toString()) }
        ?.use { JSON.readTree(it) as ObjectNode }
      ?: throw FileNotFoundException("target schema file $path not found in filesystem or jar resources")
}
