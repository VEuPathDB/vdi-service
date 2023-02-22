package org.veupathdb.service.vdi.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DatasetOwnershipFilter {
  ANY,
  OWNED,
  SHARED,
  ;

  @JsonValue
  override fun toString() = name.lowercase()

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized DatasetOwnershipFilter value $value")

    fun fromStringOrNull(value: String): DatasetOwnershipFilter? {
      val lowercase = value.lowercase()

      for (enumValue in values())
        if (enumValue.toString() == lowercase)
          return enumValue

      return null
    }
  }
}