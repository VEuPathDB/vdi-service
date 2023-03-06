package org.veupathdb.service.vdi.util

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import vdi.components.json.JSON

// TODO: relocate me
class ValidationErrors {
  private val byKey   = HashMap<String, MutableList<String>>()
  private val general = ArrayList<String>()

  val isEmpty
    get() = byKey.isEmpty() && general.isEmpty()

  val isNotEmpty
    get() = byKey.isNotEmpty() || general.isNotEmpty()

  fun add(key: String, error: String) {
    byKey.computeIfAbsent(key) { ArrayList() }
      .add(error)
  }

  fun add(error: String) {
    general.add(error)
  }

  @JsonValue
  fun toJson(): JsonNode =
    JSON.createObjectNode()
      .apply {
        set<JsonNode>("general", general.toJson())
        set<JsonNode>("byKey", JSON.createObjectNode()
          .apply { byKey.forEach { (k, v) -> set<JsonNode>(k, v.toJson()) } })
      }

  fun throwIfNotEmpty() {
    if (isNotEmpty)
      throw UnprocessableEntityException(general, byKey)
  }
}

private fun MutableList<String>.toJson() = JSON.createArrayNode()
  .also { forEach(it::add) }