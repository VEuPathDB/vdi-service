package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkInRange
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckLength
import vdi.service.rest.generated.model.*
import vdi.service.rest.server.conversion.DatasetSourceConverter

val DatasetNameLengthRange = 3..1024
val OriginLengthRange = 3..256
val ProgramNameLengthRange = OriginLengthRange
val ProjectNameLengthRange = ProgramNameLengthRange
val ShortAttributionLengthRange = 3..40
val SummaryLengthRange = 3..4000 // max size for varchar in oracle

fun DatasetMetaBase.cleanup() {
  cleanupDistinctList(::getInstallTargets, String?::cleanup)
  cleanupString(::getName)
  cleanupString(::getSummary)
  cleanupString(::getDescription)
  cleanupString(::getOrigin)
  cleanupDistinctList(::getDependencies, DatasetDependency?::cleanup)
  cleanupDistinctList(::getPublications, DatasetPublication?::cleanup)
  cleanupDistinctList(::getContacts, DatasetContact?::cleanup)
  cleanupString(::getProjectName)
  cleanupString(::getProgramName)
  cleanupDistinctList(::getLinkedDatasets, LinkedDataset?::cleanup)
  cleanup(::getExperimentalOrganism, DatasetOrganism?::cleanup)
  cleanup(::getHostOrganism, DatasetOrganism?::cleanup)
  cleanup(::getDatasetCharacteristics, DatasetCharacteristics?::cleanup)
  cleanup(::getExternalIdentifiers, ExternalIdentifiers?::cleanup)
  cleanupDistinctList(::getFunding, DatasetFundingAward?::cleanup)
  cleanupString(::getShortAttribution)
  cleanupString(::getDataDisclaimer)
  cleanupIfNotNull(::getDatasetSources, DatasetSourceConverter::cleanup)
}

fun DatasetMetaBase.validate(strict: Boolean, errors: ValidationErrors) {
  val root = newJsonPath(JsonField.DETAILS)

  installTargets.validateProjects(root..JsonField.INSTALL_TARGETS, errors)
  name.reqCheckLength(root..JsonField.NAME, DatasetNameLengthRange, errors)
  summary.reqCheckLength(root..JsonField.SUMMARY, SummaryLengthRange, errors)
  // description - no validation
  origin.reqCheckLength(root..JsonField.ORIGIN, OriginLengthRange, errors)
  dependencies.validate(root..JsonField.DEPENDENCIES, errors)
  publications.validate(root..JsonField.PUBLICATIONS, errors)
  contacts.validate(root..JsonField.CONTACTS, strict, errors)
  projectName?.checkLength(root..JsonField.PROJECT_NAME, ProjectNameLengthRange, errors)
  programName?.checkLength(root..JsonField.PROGRAM_NAME, ProgramNameLengthRange, errors)
  linkedDatasets.validate(root..JsonField.LINKED_DATASETS, errors)
  experimentalOrganism?.validate(root..JsonField.EXPERIMENTAL_ORGANISM, errors)
  hostOrganism.validate(root..JsonField.HOST_ORGANISM, errors)
  datasetCharacteristics?.validate(root..JsonField.DATASET_CHARACTERISTICS, errors)
  externalIdentifiers?.validate(root..JsonField.EXTERNAL_IDENTIFIERS, errors)
  funding.validate(root..JsonField.FUNDING, errors)
  shortAttribution?.checkLength(root..JsonField.SHORT_ATTRIBUTION, ShortAttributionLengthRange, errors)
  daysForApproval?.checkInRange(root..JsonField.DAYS_FOR_APPROVAL, -1, 365, errors)
  datasetSources?.also { DatasetSourceConverter.validate(it, root..JsonField.DATASET_SOURCES, errors) }
}
