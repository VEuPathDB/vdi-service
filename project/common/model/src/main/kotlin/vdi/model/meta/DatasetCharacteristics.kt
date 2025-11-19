package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.meta.VersionedMetaObject

/**
 * @since v1.7.0
 */
data class DatasetCharacteristics(
  @param:JsonProperty(StudyDesign)
  @field:JsonProperty(StudyDesign)
  val studyDesign: String? = null,

  @param:JsonProperty(StudyType)
  @field:JsonProperty(StudyType)
  val studyType: String? = null,

  @param:JsonProperty(Countries)
  @field:JsonProperty(Countries)
  val countries: List<String> = emptyList(),

  @param:JsonProperty(Years)
  @field:JsonProperty(Years)
  val years: SampleYearRange? = null,

  @param:JsonProperty(StudySpecies)
  @field:JsonProperty(StudySpecies)
  val studySpecies: List<String> = emptyList(),

  /**
   * Diseases or health conditions.
   */
  @param:JsonProperty(Diseases)
  @field:JsonProperty(Diseases)
  val diseases: List<String> = emptyList(),

  @param:JsonProperty(AssociatedFactors)
  @field:JsonProperty(AssociatedFactors)
  val associatedFactors: List<String> = emptyList(),

  @param:JsonProperty(ParticipantAges)
  @field:JsonProperty(ParticipantAges)
  val participantAges: String? = null,

  @param:JsonProperty(SampleTypes)
  @field:JsonProperty(SampleTypes)
  val sampleTypes: List<String> = emptyList(),
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
