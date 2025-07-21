package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetFieldSample(
  @field:JsonProperty(Countries)
  val countries: Set<String> = emptySet(),

  @field:JsonProperty(Years)
  val years: SampleYearRange? = null,

  @field:JsonProperty(ParticipantAges)
  val participantAges: String? = null,

  @field:JsonProperty(SampleTypes)
  val sampleTypes: Set<String> = emptySet(),
) {
  companion object JsonKey {
    const val Countries       = "countries"
    const val Years           = "years"
    const val ParticipantAges = "participantAges"
    const val SampleTypes     = "sampleTypes"
  }
}
