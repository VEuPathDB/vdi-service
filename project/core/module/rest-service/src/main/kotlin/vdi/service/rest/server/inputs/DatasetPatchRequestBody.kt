@file:JvmName("DatasetPatchRequestInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.*
import vdi.model.data.DatasetCharacteristics
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.BioprojectIDReference
import vdi.service.rest.generated.model.DOIReference
import vdi.service.rest.generated.model.DatasetContact
import vdi.service.rest.generated.model.DatasetFundingAward
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.LinkedDataset
import vdi.service.rest.generated.model.SampleYearRange
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

  characteristics?.apply {
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
  shortAttribution?.apply { cleanupString(::getValue) }
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

      val originalHandler = PluginRegistry[original.type]

      if (originalHandler == null)
        errors.add(JF.DATASET_TYPE, "original dataset type is disabled")
      else if (!originalHandler.changesEnabled)
        errors.add(JF.DATASET_TYPE, "cannot change dataset type from ${original.type}")
      else if (PluginRegistry[DatasetType(DataType.of(name), version)] == null)
        errors.add(JF.DATASET_TYPE, "no installers available for given dataset type")
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

  characteristics?.validate(original.characteristics, errors)
  externalIdentifiers?.validate(JF.EXTERNAL_IDENTIFIERS, errors)

  funding?.value?.validate(JF.FUNDING, errors)
  shortAttribution?.value?.checkLength(JF.SHORT_ATTRIBUTION, ShortAttributionLengthRange, errors)

  return errors
}

private inline fun <T: Any> T?.requireAnd(jPath: String, errors: ValidationErrors, validator: T.() -> Unit) {
  if (this == null)
    errors.add(jPath, "field cannot be unset")
  else
    validator(this)
}


internal fun DatasetPatchRequestBody.applyPatch(
  original: DatasetMetadata,
  revisionHistory: DatasetRevisionHistory? = original.revisionHistory,
) =
  DatasetMetadata(
    type                 = type?.toInternal() ?: original.type,
    installTargets       = original.installTargets,
    visibility           = visibility.unsafePatch(original.visibility, vdi.service.rest.generated.model.DatasetVisibility::toInternal),
    owner                = original.owner,
    name                 = name.unsafePatch(original.name),
    summary              = summary.unsafePatch(original.summary),
    origin               = original.origin,
    created              = original.created,
    description          = description.unsafePatch(original.description),
    shortAttribution     = shortAttribution.unsafePatch(original.shortAttribution),
    sourceURL            = original.sourceURL,
    dependencies         = original.dependencies,
    publications         = publications.unsafePatch(original.publications, List<DatasetPublication>::toInternal),
    externalIdentifiers  = externalIdentifiers.applyPatch(original.externalIdentifiers),
    contacts             = contacts.unsafePatch(original.contacts, Iterable<DatasetContact>::toInternal),
    revisionHistory      = revisionHistory,
    projectName          = projectName.unsafePatch(original.projectName),
    programName          = programName.unsafePatch(original.programName),
    linkedDatasets       = linkedDatasets.unsafePatch(original.linkedDatasets, Iterable<LinkedDataset>::toInternal),
    experimentalOrganism = experimentalOrganism.unsafePatch(original.experimentalOrganism),
    hostOrganism         = hostOrganism.unsafePatch(original.hostOrganism),
    characteristics      = characteristics.applyPatch(original.characteristics),
    funding              = funding.unsafePatch(original.funding, Iterable<DatasetFundingAward>::toInternal),
  )

fun DatasetTypePatch.toInternal() = DatasetType(DataType.of(value.name), value.version)

private fun DatasetCharacteristicsPatch.validate(original: DatasetCharacteristics?, errors: ValidationErrors) {

  // If the client is attempting to change the study design value
  if (studyDesign != null) {
    when {
      // If the client explicitly set the study design value to null
      studyDesign.value == null -> {
        // then the study type must also be set to null (study type requires study design)
        if (studyType == null || studyType.value != null)
          errors.add(JF.CHARACTERISTICS..JF.STUDY_TYPE, "cannot remove study design without also removing study type")
      }

      // If the study design has been set, AND no study type value was provided
      studyType == null -> {
        // then the original must already have a study type value
        original?.studyType.require(JF.CHARACTERISTICS..JF.STUDY_TYPE, errors) {}
      }

      // If the study design has been set, AND the client is trying to remove out the study type value.
      studyType.value == null -> {
        // No.
        errors.add(JF.CHARACTERISTICS..JF.STUDY_TYPE)
      }
    }

  // If the client is attempting to change the study type value
  } else if (studyType != null) {
    when {
      // we already know the client didn't attempt to change the study design
      // value by virtue of being in this else block.
      studyType.value == null -> {
        null.require(JF.CHARACTERISTICS..JF.STUDY_DESIGN, errors) {}
      }

      // If the client is attempting to change the study type value without also
      // providing a study design value
      else -> {
        // then the action is only valid if we already had a study design value.
        original?.studyDesign.require(JF.CHARACTERISTICS..JF.STUDY_DESIGN, errors) {}
      }
    }
  }

  countries?.value?.validateCountries(JF.CHARACTERISTICS, errors)

  years?.value?.validate(JF.CHARACTERISTICS..JF.YEARS, errors)

  studySpecies?.value?.validateStudySpecies(JF.CHARACTERISTICS, errors)
  diseases?.value?.validateDiseases(JF.CHARACTERISTICS, errors)
  associatedFactors?.value?.validateAssociatedFactors(JF.CHARACTERISTICS, errors)
  participantAges?.value?.validateParticipantAges(JF.CHARACTERISTICS, errors)
  sampleTypes?.value?.validateSampleTypes(JF.CHARACTERISTICS, errors)
}
