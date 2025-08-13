@file:JvmName("DatasetMetaBaseValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import vdi.service.rest.generated.model.*

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
  cleanupDistinctList(::getRelatedStudies, RelatedStudy?::cleanup)
  cleanup(::getExperimentalOrganism, DatasetOrganism?::cleanup)
  cleanup(::getHostOrganism, DatasetOrganism?::cleanup)
  cleanup(::getStudyCharacteristics, StudyCharacteristics?::cleanup)
  cleanup(::getExternalIdentifiers, ExternalIdentifiers?::cleanup)
  cleanupDistinctList(::getFunding, DatasetFundingAward?::cleanup)
}

fun DatasetMetaBase.validate(strict: Boolean, errors: ValidationErrors) {
  installTargets.validateProjects(JsonField.META..JsonField.INSTALL_TARGETS, errors)
  name.validateName(JsonField.META..JsonField.NAME, errors)
  summary.validateSummary(JsonField.META..JsonField.SUMMARY, errors)
  // description - no validation
  origin.validateOrigin(JsonField.META..JsonField.ORIGIN, errors)
  dependencies.validate(JsonField.META..JsonField.DEPENDENCIES, errors)
  publications.validate(JsonField.META..JsonField.PUBLICATIONS, errors)
  contacts.validate(JsonField.META..JsonField.CONTACTS, strict, errors)
  projectName?.checkLength(JsonField.META..JsonField.PROJECT_NAME, 1, 256, errors)
  programName?.checkLength(JsonField.META..JsonField.PROGRAM_NAME, 1, 256, errors)
  relatedStudies.validate(JsonField.META..JsonField.RELATED_STUDIES, errors)
  experimentalOrganism.validate(JsonField.META..JsonField.EXPERIMENTAL_ORGANISM, errors)
  hostOrganism.validate(JsonField.META..JsonField.HOST_ORGANISM, errors)
  studyCharacteristics.validate(JsonField.META..JsonField.STUDY_CHARACTERISTICS, errors)
  externalIdentifiers.validate(JsonField.META..JsonField.EXTERNAL_IDENTIFIERS, errors)
  funding.validate(JsonField.META..JsonField.FUNDING, errors)
}
