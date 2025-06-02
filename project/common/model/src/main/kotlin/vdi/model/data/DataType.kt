package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

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
