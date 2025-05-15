package vdi.lib.config.common

import com.fasterxml.jackson.databind.node.ObjectNode
import org.veupathdb.vdi.lib.json.JSON

internal object NullSchema : ObjectNode(JSON.nodeFactory) {
  private fun readResolve(): Any = NullSchema

  init {
    put("\$schema", "https://json-schema.org/draft/2020-12/schema")
    put("type", "null")
  }
}
