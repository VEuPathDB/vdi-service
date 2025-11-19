package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class ValidationResponse(
  @param:JsonProperty(IsValid)
  @field:JsonProperty(IsValid)
  val isValid: Boolean,

  @param:JsonProperty(Basic)
  @field:JsonProperty(Basic)
  val basicWarnings: List<String>,

  @param:JsonProperty(Community)
  @field:JsonProperty(Community)
  val communityWarnings: List<String>,
) {
  companion object JsonKey {
    const val IsValid = "isValid"
    const val Basic = "basicValidation"
    const val Community = "communityValidation"
  }
}

