package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import java.net.URI
import java.time.OffsetDateTime
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility

fun DatasetProxyPostMeta.cleanup() {
  (this as DatasetPostMeta).cleanup()
  type.cleanup()
  visibility = visibility ?: APIVisibility.PRIVATE
}

fun DatasetProxyPostMeta.validate(errors: ValidationErrors) {
  type.validate(JsonField.META..JsonField.TYPE, installTargets, errors)

  (this as DatasetPostMeta).validate(errors)
}

fun DatasetProxyPostMeta.toInternal(userID: UserID, url: String?) =
  DatasetMetadata(
    type                 = type.toInternal(),
    installTargets       = installTargets.toSet(),
    visibility           = visibility.toInternal(),
    owner                = userID,
    name                 = name,
    summary              = summary,
    description          = description,
    origin               = origin,
    created              = createdOn ?: OffsetDateTime.now(),
    sourceURL            = url?.let(URI::create),
    dependencies         = dependencies.toInternalDistinct(DatasetDependency::toInternal),
    publications         = publications.toInternalDistinct(DatasetPublication::toInternal),
    contacts             = contacts.toInternalDistinct(DatasetContact::toInternal),
    shortAttribution     = shortAttribution,
    projectName          = projectName,
    programName          = programName,
    linkedDatasets       = linkedDatasets.toInternalDistinct(LinkedDataset::toInternal),
    studyCharacteristics = studyCharacteristics?.toInternal(),
    externalIdentifiers  = externalIdentifiers?.toInternal(),
    funding              = funding.toInternalDistinct(DatasetFundingAward::toInternal),
    daysForApproval      = daysForApproval ?: -1,
    dataDisclaimer       = dataDisclaimer,
  )
