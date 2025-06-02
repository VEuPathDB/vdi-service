package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

open class DatasetShareReceipt(
  @JsonProperty("action")
  val action: Action
) {
  enum class Action {
    Accept, Reject;

    @JsonValue
    override fun toString() = name.lowercase()

    companion object {
      @JvmStatic
      @JsonCreator
      fun fromString(value: String) =
        fromStringOrNull(value)
          ?: throw IllegalArgumentException("unrecognized DatasetShareReceipt.Action value: $value")

      @JvmStatic
      fun fromStringOrNull(value: String): Action? {
        for (enum in entries)
          if (enum.toString() == value)
            return enum

        return null
      }
    }
  }
}
