package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DatasetVisibility {
  Private,
  Protected,
  Public,
  ;

  @get:JsonValue
  val value
    get() = when (this) {
      Private    -> "private"
      Protected  -> "protected"
      Public     -> "public"
    }

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("Unrecognized DatasetVisibility value: $value")

    @JvmStatic
    fun fromStringOrNull(value: String): DatasetVisibility? =
      value.lowercase().let { entries.firstOrNull { e -> e.value == it } }
  }
}
