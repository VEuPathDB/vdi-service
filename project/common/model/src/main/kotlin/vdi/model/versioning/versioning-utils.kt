package vdi.model.versioning

import com.fasterxml.jackson.databind.node.ObjectNode

internal fun ObjectNode.move(oldKey: String, newKey: String) =
  apply {
    set<ObjectNode>(newKey, get(oldKey))
    remove(oldKey)
  }