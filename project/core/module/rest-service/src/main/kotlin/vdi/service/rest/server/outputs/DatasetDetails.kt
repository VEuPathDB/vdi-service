@file:JvmName("DatasetDetailsOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetShare
import vdi.core.db.cache.model.RelatedDataset
import vdi.model.data.*
import vdi.service.rest.generated.model.DatasetDetails
import vdi.service.rest.generated.model.DatasetDetailsImpl
import vdi.service.rest.model.UserDetails

/**
 * API type conversion for use when the dataset metadata is available from the
 * object store.
 */
internal fun DatasetDetails(
  datasetID: DatasetID,
  meta: DatasetMetadata,
  importStatus: DatasetImportStatus?,
  importMessages: List<String>,
  shares: List<DatasetShare>,
  installs: Map<InstallTargetID, InstallStatuses>,
  userInfo: Map<UserID, UserDetails>,
  relatedDatasets: Sequence<RelatedDataset>,
): DatasetDetails =
  DatasetDetailsImpl().apply {
    datasetId            = datasetID.toString()
    owner                = DatasetOwner(userInfo[meta.owner]!!)
    type                 = DatasetTypeOutput(meta.type)
    name                 = meta.name
    summary              = meta.summary
    description          = meta.description
    visibility           = DatasetVisibility(meta.visibility)
    installTargets       = meta.installTargets.toList()
    origin               = meta.origin
    dependencies         = meta.dependencies.map(::DatasetDependency)
    publications         = meta.publications.map(::DatasetPublication)
    contacts             = meta.contacts.map(::DatasetContact)
    projectName          = meta.projectName
    programName          = meta.programName
    experimentalOrganism = meta.experimentalOrganism?.let(::DatasetOrganism)
    hostOrganism         = meta.hostOrganism?.let(::DatasetOrganism)
    studyCharacteristics = meta.characteristics?.let(::StudyCharacteristics)
    externalIdentifiers  = meta.externalIdentifiers?.let(::ExternalIdentifiers)
    funding              = meta.funding.map(::DatasetFundingAward)
    created              = meta.created
    sourceUrl            = meta.sourceURL?.toString()
    revisionHistory      = meta.revisionHistory?.let(::RevisionHistory)
    this.importMessages  = importMessages
    this.shares          = shares.map { ShareOffer(userInfo[it.recipientID]!!, it.offerStatus!!) }
    status               = importStatus?.let { DatasetStatusInfo(it, installs) }
    linkedDatasets       = meta.linkedDatasets.map(::vdi.service.rest.server.outputs.LinkedDataset)
    this.relatedDatasets = relatedDatasets.map(::RelatedDatasetInfo).toList()
  }

