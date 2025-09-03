@file:JvmName("DatasetPatchRequestInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetVisibility
import vdi.model.data.ExternalDatasetIdentifiers
import vdi.model.data.StudyCharacteristics
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility
import vdi.service.rest.generated.model.JsonField as JF

internal fun DatasetPatchRequestBody.cleanup() {
  type?.apply { cleanup(::getValue, DatasetTypeInput?::cleanup) }
  name?.apply { cleanupString(::getValue) }
  summary?.apply { cleanupString(::getValue) }
  description?.apply { cleanupString(::getValue) }
  publications?.apply { cleanupList(::getValue, DatasetPublication?::cleanup) }

  contacts?.apply { cleanupList(::getValue, DatasetContact?::cleanup) }
  projectName?.apply { cleanupString(::getValue) }
  programName?.apply { cleanupString(::getValue) }
  linkedDatasets?.apply { cleanupList(::getValue, LinkedDataset?::cleanup) }

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

internal fun DatasetPatchRequestBody.validate(
  original: DatasetMetadata,
  errors: ValidationErrors = ValidationErrors()
): ValidationErrors {
  var isPublic = original.visibility != DatasetVisibility.Private

  visibility?.apply { value.requireAnd(JF.VISIBILITY, errors) {
    if (this != APIVisibility.PRIVATE)
      isPublic = true
  } }

  type?.apply {
    value.requireAnd(JF.DATASET_TYPE, errors) {
      validate(JF.DATASET_TYPE, original.installTargets, errors)

      val handler = PluginRegistry[original.type]

      if (handler == null)
        errors.add(JF.DATASET_TYPE, "original dataset type is disabled")
      else if (!handler.changesEnabled)
        errors.add(JF.DATASET_TYPE, "cannot change dataset type from ${original.type}")
    }
  }

  name?.apply { value.requireAnd(JF.NAME, errors) { checkLength(JF.NAME, DatasetNameLengthRange, errors) } }

  summary?.apply { value.requireAnd(JF.SUMMARY, errors) {
    checkLength(JF.SUMMARY, SummaryLengthRange, errors)
  } }

  // description (nothing to validate)
  publications?.value?.validate(JF.PUBLICATIONS, errors)

  contacts?.apply {
    if (isPublic) {
      // Contacts are required for public datasets.
      value.requireAnd(JF.CONTACTS, errors) {
        validate(JF.CONTACTS, true, errors)
        if (size < 0)
          errors.add(JF.CONTACTS, "must provide at least one contact for non-private datasets")
      }
    } else {
      value?.validate(JF.CONTACTS, false, errors)
    }
  }

  projectName?.value?.checkLength(JF.PROJECT_NAME, ProjectNameLengthRange, errors)
  programName?.value?.checkLength(JF.PROGRAM_NAME, ProgramNameLengthRange, errors)
  linkedDatasets?.value?.validate(JF.LINKED_DATASETS, errors)

  studyCharacteristics?.validate(original.studyCharacteristics, errors)
  externalIdentifiers?.validate(errors)

  funding?.value?.validate(JF.FUNDING, errors)

  return errors
}

private inline fun <T: Any> T?.requireAnd(jPath: String, errors: ValidationErrors, validator: T.() -> Unit) {
  if (this == null)
    errors.add(jPath, "field cannot be unset")
  else
    validator(this)
}


internal fun DatasetPatchRequestBody.applyPatch(original: DatasetMetadata) =
  DatasetMetadata(
    type            = type?.value?.toInternal() ?: original.type,
    installTargets  = original.installTargets,
    visibility      = visibility?.value?.toInternal() ?: original.visibility,
    owner           = original.owner,
    name            = name?.value ?: original.name,
    summary         = summary?.value ?: original.summary,
    description     = description.mapIfPresent(original.description) { value },
    origin          = original.origin,
    created         = original.created,
    sourceURL       = original.sourceURL,
    dependencies    = original.dependencies,
    projectName     = projectName.mapIfPresent(original.projectName) { value },
    programName     = programName.mapIfPresent(original.programName) { value },
    hostOrganism    = original.hostOrganism,
    revisionHistory = original.revisionHistory,

    experimentalOrganism = original.experimentalOrganism,

    publications = publications.mapIfPresent(original.publications) {
      value?.toInternalDistinct(DatasetPublication::toInternal) ?: emptyList()
    },

    contacts = contacts.mapIfPresent(original.contacts) {
      value?.toInternalDistinct(DatasetContact::toInternal) ?: emptyList()
    },

    linkedDatasets = linkedDatasets.mapIfPresent(original.linkedDatasets) {
      value?.toInternalDistinct(LinkedDataset::toInternal) ?: emptyList()
    },

    studyCharacteristics = studyCharacteristics.mapIfPresent(original.studyCharacteristics) {
      applyPatch(original.studyCharacteristics)
    },

    externalIdentifiers = externalIdentifiers.mapIfPresent(original.externalIdentifiers) {
      applyPatch(original.externalIdentifiers)
    },

    funding = funding.mapIfPresent(original.funding) {
      value?.toInternalDistinct(DatasetFundingAward::toInternal) ?: emptyList()
    },
  )

private fun StudyCharacteristicsPatch.validate(original: StudyCharacteristics?, errors: ValidationErrors) {

  // If the client is attempting to change the study design value
  if (studyDesign != null) {
    when {
      // If the client explicitly set the study design value to null
      studyDesign.value == null -> {
        // then the study type must also be set to null (study type requires study design)
        if (studyType == null || studyType.value != null)
          errors.add(JF.STUDY_CHARACTERISTICS..JF.STUDY_TYPE, "cannot remove study design without also removing study type")
      }

      // If the study design has been set, AND no study type value was provided
      studyType == null -> {
        // then the original must already have a study type value
        original?.studyType.require(JF.STUDY_CHARACTERISTICS..JF.STUDY_TYPE, errors) {}
      }

      // If the study design has been set, AND the client is trying to remove out the study type value.
      studyType.value == null -> {
        // No.
        errors.add(JF.STUDY_CHARACTERISTICS..JF.STUDY_TYPE)
      }
    }

  // If the client is attempting to change the study type value
  } else if (studyType != null) {
    when {
      // we already know the client didn't attempt to change the study design
      // value by virtue of being in this else block.
      studyType.value == null -> {
        null.require(JF.STUDY_CHARACTERISTICS..JF.STUDY_DESIGN, errors) {}
      }

      // If the client is attempting to change the study type value without also
      // providing a study design value
      else -> {
        // then the action is only valid if we already had a study design value.
        original?.studyDesign.require(JF.STUDY_CHARACTERISTICS..JF.STUDY_DESIGN, errors) {}
      }
    }
  }

  countries?.value?.validateCountries(JF.STUDY_CHARACTERISTICS, errors)

  years?.value?.validate(JF.STUDY_CHARACTERISTICS..JF.YEARS, errors)

  studySpecies?.value?.validateStudySpecies(JF.STUDY_CHARACTERISTICS, errors)
  diseases?.value?.validateDiseases(JF.STUDY_CHARACTERISTICS, errors)
  associatedFactors?.value?.validateAssociatedFactors(JF.STUDY_CHARACTERISTICS, errors)
  participantAges?.value?.validateParticipantAges(JF.STUDY_CHARACTERISTICS, errors)
  sampleTypes?.value?.validateSampleTypes(JF.STUDY_CHARACTERISTICS, errors)
}

private fun ExternalIdentifiersPatch.validate(errors: ValidationErrors) {
  dois?.value?.validate(JF.EXTERNAL_IDENTIFIERS..JF.DOIS, errors)
  hyperlinks?.value?.validate(JF.EXTERNAL_IDENTIFIERS..JF.HYPERLINKS, errors)
  bioprojectIds?.value?.validate(JF.EXTERNAL_IDENTIFIERS..JF.BIOPROJECT_IDS, errors)
}

private fun StudyCharacteristicsPatch.applyPatch(original: StudyCharacteristics?) =
  StudyCharacteristics(
    studyDesign       = studyDesign.mapIfPresent(original?.studyDesign) { value },
    studyType         = studyType.mapIfPresent(original?.studyType) { value },
    countries         = countries.mapIfPresent(original?.countries) { value } ?: emptyList(),
    years             = years.mapIfPresent(original?.years) { value?.toInternal() },
    studySpecies      = studySpecies.mapIfPresent(original?.studySpecies) { value } ?: emptyList(),
    diseases          = diseases.mapIfPresent(original?.diseases) { value } ?: emptyList(),
    associatedFactors = associatedFactors.mapIfPresent(original?.associatedFactors) { value } ?: emptyList(),
    participantAges   = participantAges.mapIfPresent(original?.participantAges) { value },
    sampleTypes       = sampleTypes?.mapIfPresent(original?.sampleTypes) { value } ?: emptyList()
  )

private fun ExternalIdentifiersPatch.applyPatch(original: ExternalDatasetIdentifiers?) =
  ExternalDatasetIdentifiers(
    dois = dois.mapIfPresent(original?.dois) {
      value?.toInternalDistinct(DOIReference::toInternal)
    } ?: emptyList(),

    hyperlinks = hyperlinks.mapIfPresent(original?.hyperlinks) {
      value?.toInternalDistinct(DatasetHyperlink::toInternal)
    } ?: emptyList(),

    bioprojectIDs = bioprojectIds.mapIfPresent(original?.bioprojectIDs) {
      value?.toInternalDistinct(BioprojectIDReference::toInternal)
    } ?: emptyList()
  )


/**
 * Applies the given mapping function to the receiver value, if it is not null,
 * otherwise returns the given fallback value.
 *
 * Used to apply either the patch input replacement
 *
 * @receiver Nullable patch value container instance.
 *
 * @param fallback Fallback/original value to use when the patch container is
 * null.
 *
 * @param fn Mapping function to apply to convert the patch value into the type
 * expected by the consumer.
 *
 * @return The result of [fn] if the receiver is not null, otherwise [fallback].
 */
private inline fun <R: Any, O> R?.mapIfPresent(fallback: O, fn: R.() -> O) =
  if (this == null)
    fallback
  else
    fn(this)
