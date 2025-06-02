package vdi.lib.db.cache.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DatasetListSortField {
  CREATION_TIMESTAMP,
  NAME,
  ;

  @JsonValue
  override fun toString() = name.lowercase()

  companion object {

    @JvmStatic
    @JsonCreator
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized DatasetListSortField value $value")

    fun fromStringOrNull(value: String): DatasetListSortField? {
      val lowercase = value.lowercase()

      for (enumValue in entries)
        if (enumValue.toString() == lowercase)
          return enumValue

      return null
    }
  }
}
