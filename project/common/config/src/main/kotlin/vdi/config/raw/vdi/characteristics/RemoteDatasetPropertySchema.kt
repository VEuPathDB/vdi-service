package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.json.JSON
import java.net.URL
import vdi.logging.MetaLogger

@JvmInline
internal value class RemoteDatasetPropertySchema(private val url: URL): DatasetPropertySchema {
  override fun resolve(): ObjectNode {
    MetaLogger.info("loading remote schema {}", url)
    return JSON.readTree(url) as ObjectNode
  }
}
