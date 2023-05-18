package org.veupathdb.service.vdi.model

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
      val value = value.lowercase()

      for (enum in values())
        if (enum.name.lowercase() == value)
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