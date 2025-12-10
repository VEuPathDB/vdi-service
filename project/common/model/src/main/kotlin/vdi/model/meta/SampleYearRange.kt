package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class SampleYearRange(
  @param:JsonProperty(Start)
  @field:JsonProperty(Start)
  val start: Short,

  @param:JsonProperty(End)
  @field:JsonProperty(End)
  val end: Short,
) {
  companion object JsonKey {
    const val Start = "start"
    const val End   = "end"
  }
}