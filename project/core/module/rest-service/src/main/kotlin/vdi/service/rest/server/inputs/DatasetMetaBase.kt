@file:JvmName("DatasetMetaBaseValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.optCheckLength
import org.veupathdb.lib.request.validation.rangeTo
import vdi.service.rest.generated.model.*

fun DatasetMetaBase.cleanup() {
  installTargets
    ?.ifEmpty { null }
    ?.asSequence()
    ?.map { it?.trim() }
    ?.distinct()
    ?.toList()
    ?: emptyList()

  name        = name.cleanupString()
  summary     = summary.cleanupString()
  description = description.cleanupString()
  origin      = origin.cleanupString()
  projectName = projectName.cleanupString()
  programName = programName.cleanupString()

  experimentalOrganism = experimentalOrganism.cleanup()
  hostOrganism         = hostOrganism.cleanup()
  studyCharacteristics = studyCharacteristics.cleanup()
  externalIdentifiers  = externalIdentifiers.cleanup()

  dependencies   = dependencies.cleanup(DatasetDependency::cleanup)
  publications   = publications.cleanup(DatasetPublication::cleanup)
  contacts       = contacts.cleanup(DatasetContact::cleanup)
  relatedStudies = relatedStudies.cleanup(RelatedStudy::cleanup)
  funding        = funding.cleanup(DatasetFundingAward::cleanup)
}

fun DatasetMetaBase.validate(errors: ValidationErrors) {
  installTargets.validateProjects(JsonField.META..JsonField.DEPENDENCIES, errors)
  name.validateName(JsonField.META..JsonField.NAME, errors)
  summary.validateSummary(JsonField.META..JsonField.SUMMARY, errors)
  // description - no validation
  origin.validateOrigin(JsonField.META..JsonField.ORIGIN, errors)
  dependencies.validate(JsonField.META..JsonField.DEPENDENCIES, errors)
  publications.validate(JsonField.META..JsonField.PUBLICATIONS, errors)
  contacts.validate(JsonField.META..JsonField.CONTACTS, errors)
  projectName.optCheckLength(JsonField.META..JsonField.PROJECT_NAME, 1, 256, errors)
  programName.optCheckLength(JsonField.META..JsonField.PROGRAM_NAME, 1, 256, errors)
  relatedStudies.validate(JsonField.META..JsonField.RELATED_STUDIES, errors)
  experimentalOrganism.validate(JsonField.META..JsonField.EXPERIMENTAL_ORGANISM, errors)
  hostOrganism.validate(JsonField.META..JsonField.HOST_ORGANISM, errors)
  studyCharacteristics.validate(JsonField.META..JsonField.STUDY_CHARACTERISTICS, errors)
  externalIdentifiers.validate(JsonField.META..JsonField.EXTERNAL_IDENTIFIERS, errors)
  funding.validate(JsonField.META..JsonField.FUNDING, errors)
}
