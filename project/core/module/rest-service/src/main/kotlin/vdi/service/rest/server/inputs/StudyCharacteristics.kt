@file:JvmName("StudyCharacteristicsApiExtensions")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.SampleYearRange
import vdi.model.data.StudyCharacteristics
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.SampleYearRange as APIYearRange
import vdi.service.rest.generated.model.StudyCharacteristics as APIStudyCharacteristics

private val CommonLengthRange = 3..128

private val CountryValidLength = CommonLengthRange
private val StudySpeciesValidLength = CommonLengthRange
private val DiseaseValidLength = CommonLengthRange
private val AssociatedFactorValidLength = CommonLengthRange
private val ParticipantAgeValidLength = CommonLengthRange
private val SampleTypeValidLength = CommonLengthRange

private const val MinYear: Short = 1500
private const val MaxYear: Short = 2500


fun APIStudyCharacteristics?.cleanup() = this?.also {
  cleanupString(::getStudyDesign)
  cleanupString(::getStudyType)
  cleanupDistinctList(::getCountries, String?::cleanup)
  cleanupDistinctList(::getStudySpecies, String?::cleanup)
  cleanupDistinctList(::getDiseases, String?::cleanup)
  cleanupDistinctList(::getAssociatedFactors, String?::cleanup)
  cleanupString(::getParticipantAges)
  cleanupDistinctList(::getSampleTypes, String?::cleanup)
}

fun APIStudyCharacteristics?.validate(jPath: String, errors: ValidationErrors) {
  if (this != null) {
    if (studyDesign != null) {
      errors.require(jPath..JsonField.STUDY_TYPE, errors) {}
    } else if (studyType != null) {
      // if study design is null, study type must also be null
      errors.add(jPath..JsonField.STUDY_TYPE, "must not be set without a \"${JsonField.STUDY_DESIGN}\" value")
    }

    countries?.reqEntriesCheckLength(jPath..JsonField.COUNTRIES, CountryValidLength, errors)

    years?.also {
      val path = jPath..JsonField.YEARS

      it.start.reqCheckInRange(path, MinYear, MaxYear, errors)
      it.end.reqCheckInRange(path, MinYear, MaxYear, errors)
    }

    studySpecies?.reqEntriesCheckLength(jPath..JsonField.STUDY_SPECIES, StudySpeciesValidLength, errors)
    diseases?.reqEntriesCheckLength(jPath..JsonField.DISEASES, DiseaseValidLength, errors)
    associatedFactors?.reqEntriesCheckLength(jPath..JsonField.ASSOCIATED_FACTORS, AssociatedFactorValidLength, errors)
    participantAges?.checkLength(jPath..JsonField.PARTICIPANT_AGES, ParticipantAgeValidLength, errors)
    sampleTypes?.reqEntriesCheckLength(jPath..JsonField.SAMPLE_TYPES, SampleTypeValidLength, errors)
  }
}

fun APIStudyCharacteristics.toInternal() =
  StudyCharacteristics(
    studyDesign  = studyDesign,
    studyType    = studyType,
    countries    = countries,
    years        = years?.toInternal(),
    studySpecies = studySpecies,
    diseases          = diseases,
    associatedFactors = associatedFactors,
    participantAges   = participantAges,
    sampleTypes       = sampleTypes,
  )

private fun APIYearRange.toInternal() = SampleYearRange(start, end)