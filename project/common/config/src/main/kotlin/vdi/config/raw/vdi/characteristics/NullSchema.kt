package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.json.JSON

internal object NullSchema: ObjectNode(JSON.nodeFactory) {
  private fun readResolve(): Any = NullSchema

  init {
    put("\$schema", "https://json-schema.org/draft/2020-12/schema")
    put("type", "null")
  }
}
