package org.veupathdb.service.vdi.util

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException
import org.veupathdb.vdi.lib.json.JSON

/**
 * Request Validation Error Bundle
 *
 * Container type for collecting errors while validating an incoming request.
 *
 * Errors are collected into two categories, general errors, and errors by key.
 * General errors are collected as a list, while by key errors are collected as
 * a map of lists by a given key.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
class ValidationErrors {
  private val byKey   = HashMap<String, MutableList<String>>()
  private val general = ArrayList<String>()

  /**
   * `true` if this [ValidationErrors] instance contains no errors.
   */
  val isEmpty
    get() = byKey.isEmpty() && general.isEmpty()

  /**
   * `true` if this [ValidationErrors] instance contains one or more errors.
   */
  val isNotEmpty
    get() = byKey.isNotEmpty() || general.isNotEmpty()

  /**
   * Adds a keyed error to this error bundle.
   */
  fun add(key: String, error: String) {
    byKey.computeIfAbsent(key) { ArrayList() }
      .add(error)
  }

  /**
   * Adds a general error to this error bundle.
   */
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