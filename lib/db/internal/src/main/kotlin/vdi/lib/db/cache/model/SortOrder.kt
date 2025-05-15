package vdi.lib.db.cache.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class SortOrder {
  ASCENDING,
  DESCENDING,
  ;

  @JsonValue
  override fun toString() = when (this) {
    ASCENDING -> "ASC"
    DESCENDING -> "DESC"
  }

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized SortOrder value $value")

    fun fromStringOrNull(value: String): SortOrder? {
      val uppercase = value.uppercase()

      for (enumValue in entries)
        if (enumValue.toString() == uppercase)
          return enumValue

      return null
    }
  }
}
