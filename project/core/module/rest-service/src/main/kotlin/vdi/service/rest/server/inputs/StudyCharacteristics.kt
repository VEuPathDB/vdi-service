@file:JvmName("StudyCharacteristicsApiExtensions")
@file:Suppress("NOTHING_TO_INLINE")

package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
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



fun APIStudyCharacteristics?.cleanup() = this?.also {
  cleanupString(::getStudyDesign)
  cleanupString(::getStudyType)
  cleanupDistinctList(::getCountries, String?::cleanup)
  cleanup(::getYears, APIYearRange?::cleanup)
  cleanupDistinctList(::getStudySpecies, String?::cleanup)
  cleanupDistinctList(::getDiseases, String?::cleanup)
  cleanupDistinctList(::getAssociatedFactors, String?::cleanup)
  cleanupString(::getParticipantAges)
  cleanupDistinctList(::getSampleTypes, String?::cleanup)
}

fun APIStudyCharacteristics.validate(jPath: String, errors: ValidationErrors) {
  if (studyDesign != null) {
    studyType.require(jPath..JsonField.STUDY_TYPE, errors) {}
  } else if (studyType != null) {
    // if study design is null, study type must also be null
    errors.add(jPath..JsonField.STUDY_TYPE, "must not be set without a \"${JsonField.STUDY_DESIGN}\" value")
  }

  countries?.reqEntriesCheckLength(jPath..JsonField.COUNTRIES, CountryValidLength, errors)

  years?.validate(jPath..JsonField.YEARS, errors)

  studySpecies?.reqEntriesCheckLength(jPath..JsonField.STUDY_SPECIES, StudySpeciesValidLength, errors)
  diseases?.reqEntriesCheckLength(jPath..JsonField.DISEASES, DiseaseValidLength, errors)
  associatedFactors?.reqEntriesCheckLength(jPath..JsonField.ASSOCIATED_FACTORS, AssociatedFactorValidLength, errors)
  participantAges?.checkLength(jPath..JsonField.PARTICIPANT_AGES, ParticipantAgeValidLength, errors)
  sampleTypes?.reqEntriesCheckLength(jPath..JsonField.SAMPLE_TYPES, SampleTypeValidLength, errors)
}

fun List<String>.validateSampleTypes(jPath: String, errors: ValidationErrors) =
  reqEntriesCheckLength(jPath..JsonField.SAMPLE_TYPES, SampleTypeValidLength, errors)

fun String.validateParticipantAges(jPath: String, errors: ValidationErrors) =
  checkLength(jPath..JsonField.PARTICIPANT_AGES, ParticipantAgeValidLength, errors)

fun List<String>.validateCountries(jPath: String, errors: ValidationErrors) =
  reqEntriesCheckLength(jPath..JsonField.COUNTRIES, CountryValidLength, errors)

fun List<String>.validateStudySpecies(jPath: String, errors: ValidationErrors) =
  reqEntriesCheckLength(jPath..JsonField.STUDY_SPECIES, StudySpeciesValidLength, errors)

fun List<String>.validateDiseases(jPath: String, errors: ValidationErrors) =
  reqEntriesCheckLength(jPath..JsonField.DISEASES, DiseaseValidLength, errors)

fun List<String>.validateAssociatedFactors(jPath: String, errors: ValidationErrors) =
  reqEntriesCheckLength(jPath..JsonField.ASSOCIATED_FACTORS, AssociatedFactorValidLength, errors)

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
