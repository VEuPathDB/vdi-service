package vdi.service.rest.model

import com.fasterxml.jackson.annotation.JsonCreator

internal enum class ShareFilterStatus {
  Open,
  Accepted,
  Rejected,
  All,
  ;

  companion object {

    @JvmStatic
    fun fromStringOrNull(value: String): ShareFilterStatus? {
      val lowercaseValue = value.lowercase()

      for (enum in entries)
        if (enum.name.lowercase() == lowercaseValue)
          return enum

      return null
    }

    @JvmStatic
    @JsonCreator
    fun fromString(value: String): ShareFilterStatus =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized ShareStatus value: $value")
  }
}
