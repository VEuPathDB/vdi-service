package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.*
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.BioprojectIDReference
import vdi.service.rest.generated.model.DOIReference
import vdi.service.rest.generated.model.DatasetContact
import vdi.service.rest.generated.model.DatasetFundingAward
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.generated.model.DatasetSource
import vdi.service.rest.generated.model.LinkedDataset
import vdi.service.rest.generated.model.SampleYearRange
import vdi.service.rest.server.conversion.DatasetSourceConverter
import vdi.service.rest.server.inputs.patch.applyPatch
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

  experimentalOrganism?.value?.cleanup()
  hostOrganism?.value?.cleanup()

  datasetCharacteristics?.apply {
    studyDesign?.apply { cleanupString(::getValue) }
    studyType?.apply { cleanupString(::getValue) }
    countries?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    years?.apply { cleanup(::getValue, SampleYearRange?::cleanup) }
    studySpecies?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
    outcomes?.apply { cleanupDistinctList(::getValue, String?::cleanup) }
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

  datasetSources?.apply { value = value?.let(DatasetSourceConverter::cleanup) }
}

internal fun DatasetPatchRequestBody.validate(
  original:  DatasetMetadata,
  jPath:     String,
  errors:    ValidationErrors = ValidationErrors(),
): ValidationErrors {
  visibility?.apply {
    value.require(jPath..JF.VISIBILITY, errors) {}
  }

  type?.apply {
    val typePath = jPath..JF.TYPE

    value.requireAnd(typePath, errors) {
      validate(typePath, original.installTargets, errors)

      val originalHandler = PluginRegistry[original.type]

      if (originalHandler == null)
        errors.add(typePath, "original dataset type is disabled")
      else if (!PluginRegistry.require(original.type).typeChangesEnabled)
        errors.add(typePath, "cannot change dataset type from ${original.type}")
      else if (PluginRegistry[DatasetType(DataType.of(name), version)] == null)
        errors.add(typePath, "no installers available for given dataset type")
    }
  }

  name?.apply { value.requireAnd(jPath..JF.NAME, errors) { checkLength(JF.NAME, DatasetNameLengthRange, errors) } }

  summary?.apply {
    value.requireAnd(jPath..JF.SUMMARY, errors) {
      checkLength(jPath..JF.SUMMARY, SummaryLengthRange, errors)
    }
  }

  contacts?.apply {
    value?.validate(jPath..JF.CONTACTS, false, errors)
  }

  projectName?.value?.checkLength(jPath..JF.PROJECT_NAME, ProjectNameLengthRange, errors)
  programName?.value?.checkLength(jPath..JF.PROGRAM_NAME, ProgramNameLengthRange, errors)
  linkedDatasets?.value?.validate(jPath..JF.LINKED_DATASETS, errors)

  experimentalOrganism?.value?.validate(jPath..JF.EXPERIMENTAL_ORGANISM, errors)
  hostOrganism?.value?.validate(jPath..JF.HOST_ORGANISM, errors)

  externalIdentifiers?.validate(jPath..JF.EXTERNAL_IDENTIFIERS, errors)

  funding?.value?.validate(jPath..JF.FUNDING, errors)
  shortAttribution?.value?.checkLength(jPath..JF.SHORT_ATTRIBUTION, ShortAttributionLengthRange, errors)

  // Field Study or Clinical Trial?
  validateDatasetCharacteristics(jPath, original, errors)

  // Includes Biological Data about Organisms?
  // validateOrganismData() TODO - experimental organism list???

  // Associated Publication Available?
  validatePublications(jPath, original, errors)

  // Available from External Source?
  validateDatasetSources(jPath, original, errors)

  // Any Reuse Considerations?
  validateDataDisclaimer(jPath, original, errors)

  return errors
}

private inline fun <T: Any> T?.requireAnd(jPath: String, errors: ValidationErrors, validator: T.() -> Unit) {
  if (this == null)
    errors.add(jPath, "field cannot be unset")
  else
    validator(this)
}

internal fun DatasetPatchRequestBody.applyPatch(
  original:        DatasetMetadata,
  revisionHistory: DatasetRevisionHistory? = original.revisionHistory,
) =
  DatasetMetadata(
    type                   = type?.toInternal() ?: original.type,
    installTargets         = original.installTargets,
    visibility             = visibility.unsafePatch(original.visibility, APIVisibility::toInternal),
    owner                  = original.owner,
    name                   = name.unsafePatch(original.name),
    summary                = summary.unsafePatch(original.summary),
    description            = description.unsafePatch(original.description),
    origin                 = original.origin,
    created                = original.created,
    sourceURL              = original.sourceURL,
    dependencies           = original.dependencies,
    publications           = publications.unsafePatch(original.publications, List<DatasetPublication>::toInternal),
    contacts               = contacts.unsafePatch(original.contacts, Iterable<DatasetContact>::toInternal),
    shortAttribution       = shortAttribution.unsafePatch(original.shortAttribution),
    projectName            = projectName.unsafePatch(original.projectName),
    programName            = programName.unsafePatch(original.programName),
    linkedDatasets         = linkedDatasets.unsafePatch(original.linkedDatasets, Iterable<LinkedDataset>::toInternal),
    experimentalOrganism   = experimentalOrganism.applyPatch(original.experimentalOrganism),
    hostOrganism           = hostOrganism.applyPatch(original.hostOrganism),
    datasetCharacteristics = datasetCharacteristics.applyPatch(original.datasetCharacteristics),
    externalIdentifiers    = externalIdentifiers.applyPatch(original.externalIdentifiers),
    funding                = funding.unsafePatch(original.funding, Iterable<DatasetFundingAward>::toInternal),
    revisionHistory        = revisionHistory,
    daysForApproval        = daysForApproval.unsafePatch(original.daysForApproval) { it: Int? -> it ?: -1 },
    dataDisclaimer         = dataDisclaimer.unsafePatch(original.dataDisclaimer),
    datasetSources         = datasetSources.unsafePatch(original.datasetSources) { it: List<DatasetSource>? ->
      it?.let(DatasetSourceConverter::toInternal) ?: emptyList()
    },
    metadataContentFlags   = metadataContentFlags.applyPatch(original.metadataContentFlags)
  )

fun DatasetTypePatch.toInternal() = DatasetType(DataType.of(value.name), value.version)

private fun DatasetPatchRequestBody.validateDatasetCharacteristics(
  jPath: String,
  originalMeta: DatasetMetadata,
  errors: ValidationErrors,
) {
  val characteristicsRequired = metadataContentFlags?.hasDatasetCharacteristics
    .coalesce(originalMeta.metadataContentFlags.hasDatasetCharacteristics)
    .isTrue

  if (datasetCharacteristics == null || datasetCharacteristics.isEmpty) {
    if (characteristicsRequired && originalMeta.datasetCharacteristics?.isEmpty == true) {
      errors.add(
        jPath..JF.METADATA_CONTENT_FLAGS..JF.HAS_DATASET_CHARACTERISTICS,
        ErrorDatasetCharacteristicsRequired,
      )
    }

    return
  }

  datasetCharacteristics.validate(
    originalMeta.datasetCharacteristics,
    jPath..JF.DATASET_CHARACTERISTICS,
    errors,
  )

  if (!datasetCharacteristics.isEmpty)
    metadataContentFlags = (metadataContentFlags ?: MetadataContentFlagsPatchImpl())
      .apply { hasDatasetCharacteristics = OptionalBooleanPatch(true) }
}

private fun DatasetPatchRequestBody.validateDatasetSources(
  jPath: String,
  originalMeta: DatasetMetadata,
  errors: ValidationErrors
) {
  if (datasetSources == null || datasetSources.value.isNullOrEmpty()) {
    if (
      metadataContentFlags?.hasDatasetSources
        .coalesce(originalMeta.metadataContentFlags.hasDatasetSources)
        .isTrue
      && originalMeta.datasetSources.isEmpty()
    ) {
      errors.add(
        jPath..JF.METADATA_CONTENT_FLAGS..JF.HAS_DATASET_SOURCES,
        DatasetSourceConverter.ErrorDatasetSourcesRequired,
      )
    }

    return
  }

  DatasetSourceConverter.validate(datasetSources.value, jPath..JF.DATASET_SOURCES, errors)

  metadataContentFlags = (metadataContentFlags ?: MetadataContentFlagsPatchImpl())
    .apply { hasDatasetSources = OptionalBooleanPatch(true) }
}

private fun DatasetPatchRequestBody.validatePublications(
  jPath: String,
  originalMeta: DatasetMetadata,
  errors: ValidationErrors,
) {
  // IF the patch request did not contain a publications value
  // OR the patch is explicitly attempting to remove any/all publications
  if (publications == null || publications.value.isNullOrEmpty()) {

    // AND (
    //   the user has marked publications as being required in this request
    //   OR
    //   the user left the metadata in a pre-existing state of requiring
    //   publications
    // )
    // AND the dataset doesn't have pre-existing publications,
    // THEN return an error
    //
    // NOTES:
    // * case 1 is a no-op if the dataset does have publications
    // * case 2 shouldn't normally be possible, but bugs abound and it's better
    //   to be safe than throw cryptic exceptions.
    if (
      metadataContentFlags?.hasPublications
        .coalesce(originalMeta.metadataContentFlags.hasPublications)
        .isTrue
      && originalMeta.publications.isEmpty()
    ) {
      errors.add(jPath..JF.METADATA_CONTENT_FLAGS..JF.HAS_PUBLICATIONS, ErrorPublicationsRequired)
    }

    return
  }

  // The patch request provided publications.
  publications.value.validate(jPath = jPath..JF.PUBLICATIONS, errors)

  // If publications are provided, make sure the content flag is set correctly.
  metadataContentFlags = (metadataContentFlags ?: MetadataContentFlagsPatchImpl())
    .apply { hasPublications = OptionalBooleanPatch(true) }
}

private fun DatasetPatchRequestBody.validateDataDisclaimer(
  jPath: String,
  originalMeta: DatasetMetadata,
  errors: ValidationErrors,
) {
  if (dataDisclaimer == null || dataDisclaimer.value.isNullOrEmpty()) {
    if (metadataContentFlags?.hasDataDisclaimer
      .coalesce(originalMeta.metadataContentFlags.hasDataDisclaimer)
      .isTrue
    && originalMeta.dataDisclaimer.isNullOrEmpty()
    ) {
      errors.add(jPath..JF.METADATA_CONTENT_FLAGS..JF.DATA_DISCLAIMER, "data disclaimer is required")
    }

    return
  }

  metadataContentFlags = (metadataContentFlags ?: MetadataContentFlagsPatchImpl())
    .apply { hasDataDisclaimer = OptionalBooleanPatch(true) }
}

private fun OptionalBooleanPatch?.coalesce(original: MetadataContentFlags.FlagState) =
  when {
    this == null -> original
    else         -> MetadataContentFlags.FlagState.fromBoolean(this.value)
  }