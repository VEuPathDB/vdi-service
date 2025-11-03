package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Inlined/compile-time-only wrapper type around data type names.
 *
 * This type is used to enforce standardized text formatting, and to remove
 * ambiguity from method calls around dataset types that would otherwise all be
 * string-based.
 */
@JvmInline
value class DataType private constructor(private val raw: String) {
  init {
    if (raw.isBlank())
      throw IllegalArgumentException("DataType cannot be constructed from a blank string")
  }

  @JsonValue
  override fun toString() = raw

  companion object {
    @JvmStatic
    @JsonCreator
    fun of(value: String) = DataType(value.lowercase())
  }
}
