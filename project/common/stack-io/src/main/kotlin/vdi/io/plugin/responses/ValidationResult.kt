package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationResult(
  @param:JsonProperty(IsValid)
  @field:JsonProperty(IsValid)
  val isValid: Boolean,

  @param:JsonProperty(Warnings)
  @field:JsonProperty(Warnings)
  val warnings: Array<String>,
) {
  companion object JsonKey {
    const val IsValid = "isValid"
    const val Warnings = "warnings"
  }
}