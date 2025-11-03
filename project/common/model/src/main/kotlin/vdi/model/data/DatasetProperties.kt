package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.json.JSON

data class DatasetProperties(@JsonValue val rawValue: ObjectNode) {
  companion object {
    @JvmStatic
    @JsonCreator
    fun wrap(rawValue: ObjectNode) = DatasetProperties(rawValue)
  }

  inline fun <reified T: Any> cast(): T =
    JSON.convertValue(rawValue, T::class.java)
}
