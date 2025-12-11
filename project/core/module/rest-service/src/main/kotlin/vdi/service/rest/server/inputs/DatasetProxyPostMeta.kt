@file:JvmName("DatasetProxyPostMetaInputAdaptor")
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
    installTargets       = installTargets.toSet(),
    name                 = name,
    summary              = summary,
    description          = description,
    origin               = origin,
    dependencies         = dependencies.toInternalDistinct(DatasetDependency::toInternal),
    publications         = publications.toInternalDistinct(DatasetPublication::toInternal),
    contacts             = contacts.toInternalDistinct(DatasetContact::toInternal),
    projectName          = projectName,
    programName          = programName,
    linkedDatasets       = linkedDatasets.toInternalDistinct(LinkedDataset::toInternal),
    experimentalOrganism = experimentalOrganism?.toInternal(),
    hostOrganism         = hostOrganism?.toInternal(),
    characteristics      = characteristics?.toInternal(),
    externalIdentifiers  = externalIdentifiers?.toInternal(),
    funding              = funding.toInternalDistinct(DatasetFundingAward::toInternal),
    type                 = type.toInternal(),
    visibility           = visibility.toInternal(),
    owner                = userID,
    created              = createdOn ?: OffsetDateTime.now(),
    sourceURL            = url?.let(URI::create),
    shortAttribution     = shortAttribution,
  )
