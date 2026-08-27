package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.meta.DatasetCharacteristics
import vdi.service.rest.generated.model.DatasetCharacteristicsPatch
import vdi.service.rest.generated.model.JsonField as JF
import vdi.service.rest.generated.model.OptionalStringPatch
import vdi.service.rest.generated.model.SampleYearRange

fun DatasetCharacteristicsPatch?.applyPatch(original: DatasetCharacteristics?) =
  when (this) {
    null -> original
    else -> DatasetCharacteristics(
      studyDesign       = studyDesign.unsafePatch(original?.studyDesign),
      studyType         = studyType.unsafePatch(original?.studyType),
      countries         = countries.unsafePatch(original?.countries ?: emptyList()),
      years             = years.unsafePatch(original?.years, SampleYearRange::toInternal),
      studySpecies      = studySpecies.unsafePatch(original?.studySpecies ?: emptyList()),
      outcomes          = outcomes.unsafePatch(original?.outcomes ?: emptyList()),
      associatedFactors = associatedFactors.unsafePatch(original?.associatedFactors ?: emptyList()),
      participantAges   = participantAges.unsafePatch(original?.participantAges),
      sampleTypes       = sampleTypes.unsafePatch(original?.sampleTypes ?: emptyList())
    )
  }

/**
 * Tests if the patch object contains any property overwrite values.
 */
internal fun DatasetCharacteristicsPatch.hasSomethingToUpdate() =
  studyDesign != null
  || studyType != null
  || countries != null
  || years != null
  || studySpecies != null
  || outcomes != null
  || associatedFactors != null
  || participantAges != null
  || sampleTypes != null

private val OptionalStringPatch?.isEmpty
  get() = this == null || value.isNullOrEmpty()

val DatasetCharacteristicsPatch.isEmpty
  get() = studyDesign.isEmpty
    && studyType.isEmpty
    && (countries == null || countries.value.isNullOrEmpty())
    && (years == null || years.value == null)
    && (studySpecies == null || studySpecies.value.isNullOrEmpty())
    && (outcomes == null || outcomes.value.isNullOrEmpty())
    && (associatedFactors == null || associatedFactors.value.isNullOrEmpty())
    && participantAges.isEmpty
    && (sampleTypes == null || sampleTypes.value.isNullOrEmpty())

fun DatasetCharacteristicsPatch.validate(
  original: DatasetCharacteristics?,
  jPath: String,
  errors: ValidationErrors,
) {
  // If the client is attempting to change the study design value
  if (studyDesign != null) {
    when {
      // If the client explicitly set the study design value to null
      studyDesign.value == null -> {
        // then the study type must also be set to null (study type requires study design)
        if (studyType == null || studyType.value != null)
          errors.add(jPath..JF.STUDY_TYPE, "cannot remove study design without also removing study type")
      }

      // If the study design has been set, AND no study type value was provided
      studyType == null -> {
        // then the original must already have a study type value
        original?.studyType.require(jPath..JF.STUDY_TYPE, errors) {}
      }

      // If the study design has been set, AND the client is trying to remove out the study type value.
      studyType.value == null -> {
        // No.
        errors.add(jPath..JF.STUDY_TYPE)
      }
    }

    // If the client is attempting to change the study type value
  } else if (studyType != null) {
    when {
      // we already know the client didn't attempt to change the study design
      // value by virtue of being in this else block.
      studyType.value == null -> {
        null.require(jPath..JF.STUDY_DESIGN, errors) {}
      }

      // If the client is attempting to change the study type value without also
      // providing a study design value
      else -> {
        // then the action is only valid if we already had a study design value.
        original?.studyDesign.require(jPath..JF.STUDY_DESIGN, errors) {}
      }
    }
  }

  countries?.value?.validateCountries(jPath, errors)

  years?.value?.validate(jPath..JF.YEARS, errors)

  studySpecies?.value?.validateStudySpecies(jPath, errors)
  outcomes?.value?.validateOutcomes(jPath, errors)
  associatedFactors?.value?.validateAssociatedFactors(jPath, errors)
  participantAges?.value?.validateParticipantAges(jPath, errors)
  sampleTypes?.value?.validateSampleTypes(jPath, errors)
}
