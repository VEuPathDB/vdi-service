package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SampleYearRange(
  @field:JsonProperty(Start)
  val start: Short,

  @field:JsonProperty(End)
  val end: Short,
) {
  companion object JsonKey {
    const val Start = "start"
    const val End   = "end"
  }
}