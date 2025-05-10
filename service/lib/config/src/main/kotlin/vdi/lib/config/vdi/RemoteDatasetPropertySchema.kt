package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.vdi.lib.json.JSON
import java.net.URL

@JvmInline
internal value class RemoteDatasetPropertySchema(private val url: URL): DatasetPropertySchema {
  override fun resolve() = JSON.readTree(url) as ObjectNode
}
