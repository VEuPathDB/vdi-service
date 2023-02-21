package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DatasetShareOfferAction {
  Grant,
  Revoke,
  ;

  @JsonValue
  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(string: String): DatasetShareOfferAction {
      val lower = string.lowercase()

      for (value in values())
        if (value.toString() == lower)
          return value

      throw IllegalArgumentException("unrecognized DatasetShareOfferAction value $string")
    }
  }
}

