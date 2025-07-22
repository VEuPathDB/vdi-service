package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Defines a common response body consisting of a JSON object containing a
 * single key "warnings" mapped to an array of strings representing warnings
 * generated during the triggering request.
 */
data class WarningResponse(@field:JsonProperty(Warnings) val warnings: Collection<String>) {
  companion object JsonKey {
    const val Warnings = "warnings"
  }
}