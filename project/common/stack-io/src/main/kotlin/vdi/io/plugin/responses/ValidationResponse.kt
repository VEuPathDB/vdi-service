package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class ValidationResponse(
  @param:JsonProperty(Basic)
  @field:JsonProperty(Basic)
  val basicValidation: ValidationResult,

  @param:JsonProperty(Community)
  @field:JsonProperty(Community)
  val communityValidation: ValidationResult,
) {
  companion object JsonKey {
    const val Basic = "basicValidation"
    const val Community = "communityValidation"
  }
}

