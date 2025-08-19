@file:JvmName("DatasetMetaBaseValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckLength
import vdi.service.rest.generated.model.*

val NameLengthRange = 3..1024
val OriginLengthRange = 3..256
val ProgramNameLengthRange = 1..256
val ProjectNameLengthRange = ProgramNameLengthRange
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
  cleanupDistinctList(::getRelatedStudies, RelatedStudy?::cleanup)
  cleanup(::getExperimentalOrganism, DatasetOrganism?::cleanup)
  cleanup(::getHostOrganism, DatasetOrganism?::cleanup)
  cleanup(::getStudyCharacteristics, StudyCharacteristics?::cleanup)
  cleanup(::getExternalIdentifiers, ExternalIdentifiers?::cleanup)
  cleanupDistinctList(::getFunding, DatasetFundingAward?::cleanup)
}

fun DatasetMetaBase.validate(strict: Boolean, errors: ValidationErrors) {
  installTargets.validateProjects(JsonField.META..JsonField.INSTALL_TARGETS, errors)
  name.reqCheckLength(JsonField.META..JsonField.NAME, NameLengthRange, errors)
  summary.reqCheckLength(JsonField.META..JsonField.SUMMARY, SummaryLengthRange, errors)
  // description - no validation
  origin.reqCheckLength(JsonField.META..JsonField.ORIGIN, OriginLengthRange, errors)
  dependencies.validate(JsonField.META..JsonField.DEPENDENCIES, errors)
  publications.validate(JsonField.META..JsonField.PUBLICATIONS, errors)
  contacts.validate(JsonField.META..JsonField.CONTACTS, strict, errors)
  projectName?.checkLength(JsonField.META..JsonField.PROJECT_NAME, ProjectNameLengthRange, errors)
  programName?.checkLength(JsonField.META..JsonField.PROGRAM_NAME, ProgramNameLengthRange, errors)
  relatedStudies.validate(JsonField.META..JsonField.RELATED_STUDIES, errors)
  experimentalOrganism.validate(JsonField.META..JsonField.EXPERIMENTAL_ORGANISM, errors)
  hostOrganism.validate(JsonField.META..JsonField.HOST_ORGANISM, errors)
  studyCharacteristics?.validate(JsonField.META..JsonField.STUDY_CHARACTERISTICS, errors)
  externalIdentifiers.validate(JsonField.META..JsonField.EXTERNAL_IDENTIFIERS, errors)
  funding.validate(JsonField.META..JsonField.FUNDING, errors)
}
