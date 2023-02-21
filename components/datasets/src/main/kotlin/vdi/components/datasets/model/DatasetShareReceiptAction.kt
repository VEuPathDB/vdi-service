package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DatasetShareReceiptAction {
  Accept,
  Reject,
  ;

  @JsonValue
  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(string: String): DatasetShareReceiptAction {
      val lower = string.lowercase()

      for (value in values())
        if (value.toString() == lower)
          return value

      throw IllegalArgumentException("unrecognized DatasetShareReceiptAction value $string")
    }
  }
}

