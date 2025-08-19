@file:JvmName("DatasetPatchRequestValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.data.DatasetMetadata
import vdi.model.data.StudyCharacteristics
import vdi.service.rest.generated.model.*

@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPatchRequestBody.cleanup() {
  type?.apply { cleanup(::getValue, DatasetTypeInput?::cleanup) }
  name?.apply { cleanupString(::getValue) }
  summary?.apply { cleanupString(::getValue) }
  description?.apply { cleanupString(::getValue) }
  publications?.apply { cleanupList(::getValue, DatasetPublication?::cleanup) }

  contacts?.apply { cleanupList(::getValue, DatasetContact?::cleanup) }
  projectName?.apply { cleanupString(::getValue) }
  programName?.apply { cleanupString(::getValue) }
  relatedStudies?.apply { cleanupList(::getValue, RelatedStudy?::cleanup) }

  studyCharacteristics?.apply {
    studyDesign?.apply { cleanupString(::getValue) }
    studyType?.apply { cleanupString(::getValue) }
    countries?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    years?.apply { cleanup(::getValue, SampleYearRange?::cleanup) }
    studySpecies?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    diseases?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    associatedFactors?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    participantAges?.apply { cleanupString(::getValue) }
    sampleTypes?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
  }

  externalIdentifiers?.apply {
    dois?.apply { cleanupList(::getValue, DOIReference?::cleanup) }
    hyperlinks?.apply { cleanupList(::getValue, DatasetHyperlink?::cleanup) }
    bioprojectIds?.apply { cleanupList(::getValue, BioprojectIDReference?::cleanup) }
  }
  funding?.apply { cleanupList(::getValue, DatasetFundingAward?::cleanup) }
}

@Suppress("DuplicatedCode")
internal fun DatasetPatchRequestBody.validate(
  original: DatasetMetadata,
  errors: ValidationErrors = ValidationErrors()
): ValidationErrors {
  type?.value?.validate(JsonField.DATASET_TYPE, original.installTargets, errors)
  name?.value?.checkLength(JsonField.NAME, NameLengthRange, errors)
  summary?.value?.checkLength(JsonField.SUMMARY, SummaryLengthRange, errors)
  // description (nothing to validate)
  publications?.value?.validate(JsonField.PUBLICATIONS, errors)

  contacts?.value?.validate(JsonField.CONTACTS, strict, errors)
  projectName?.value?.checkLength(JsonField.PROJECT_NAME, ProjectNameLengthRange, errors)
  programName?.value?.checkLength(JsonField.PROGRAM_NAME, ProgramNameLengthRange, errors)
  relatedStudies?.value?.validate(JsonField.RELATED_STUDIES, errors)

  studyCharacteristics?.validate(errors)
  externalIdentifiers?.validate(errors)

  funding?.value?.validate(JsonField.FUNDING, errors)

  return errors
}

private fun StudyCharacteristicsPatch.validate(original: StudyCharacteristics, errors: ValidationErrors) {

  // If the client is attempting to change the study design value
  if (studyDesign != null) {
    when {
      // If the client explicitly set the study design value to null
      studyDesign.value == null || studyDesign.action == PatchAction.REMOVE -> {
        // then the study type must also be set to null (study type requires study design)
        if (studyType == null || studyType.value != null)
          errors.add(JsonField.STUDY_CHARACTERISTICS..JsonField.STUDY_TYPE, "cannot remove study design without also removing study type")
      }

      // If the study design has been set, AND no study type value was provided
      studyType == null -> {
        // then the original must already have a study type value
        original.studyType.require(JsonField.STUDY_CHARACTERISTICS..JsonField.STUDY_TYPE, errors) {}
      }

      // If the study design has been set, AND the client is trying to remove out the study type value.
      studyType.value == null || studyType.action == PatchAction.REMOVE -> {
        // No.
        errors.add(JsonField.STUDY_CHARACTERISTICS..JsonField.STUDY_TYPE)
      }
    }

  // If the client is attempting to change the study type value
  } else if (studyType != null) {
    when {
      // we already know the client didn't attempt to change the study design
      // value by virtue of being in this else block.
      studyType.value == null || studyType.action == PatchAction.REMOVE -> {
        null.require(JsonField.STUDY_CHARACTERISTICS..JsonField.STUDY_DESIGN, errors) {}
      }

      // If the client is attempting to change the study type value without also
      // providing a study design value
      else -> {
        // then the action is only valid if we already had a study design value.
        original.studyDesign.require(JsonField.STUDY_CHARACTERISTICS..JsonField.STUDY_DESIGN, errors) {}
      }
    }
  }



    } else if (studyType == null) {

      // AND the current stored state also has no study type value, then a study
      // type value is required to be provided.

    } else {}
  }

  if (studyDesign != null) {
    if (stud)

    if (original.studyType == null && )
  }
}

private fun ExternalIdentifiersPatch.validate(errors: ValidationErrors) {

}