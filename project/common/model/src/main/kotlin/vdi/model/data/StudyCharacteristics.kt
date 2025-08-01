package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class StudyCharacteristics(
  @field:JsonProperty(StudyDesign)
  val studyDesign: String? = null,

  @field:JsonProperty(StudyType)
  val studyType: String? = null,

  @field:JsonProperty(Countries)
  val countries: Set<String> = emptySet(),

  @field:JsonProperty(Years)
  val years: SampleYearRange? = null,

  @field:JsonProperty(StudySpecies)
  val studySpecies: Set<String> = emptySet(),

  /**
   * Diseases or health conditions.
   */
  @field:JsonProperty(Diseases)
  val diseases: Set<String> = emptySet(),

  @field:JsonProperty(AssociatedFactors)
  val associatedFactors: Set<String> = emptySet(),

  @field:JsonProperty(ParticipantAges)
  val participantAges: String? = null,

  @field:JsonProperty(SampleTypes)
  val sampleTypes: Set<String> = emptySet(),
) {
  companion object JsonKey {
    const val AssociatedFactors = "associatedFactors"
    const val Countries         = "countries"
    const val Diseases          = "diseases"
    const val ParticipantAges   = "participantAges"
    const val SampleTypes       = "sampleTypes"
    const val StudyDesign       = "studyDesign"
    const val StudySpecies      = "studySpecies"
    const val StudyType         = "studyType"
    const val Years             = "years"
  }
}
