package vdi.lib.config.vdi

import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.vdi.lib.json.JSON
import java.net.URL
import vdi.lib.logging.MetaLogger

@JvmInline
internal value class RemoteDatasetPropertySchema(private val url: URL): DatasetPropertySchema {
  override fun resolve(): ObjectNode {
    MetaLogger.info("loading remote schema {}", url)
    return JSON.readTree(url) as ObjectNode
  }
}
