package vdi.service.rest.server.outputs

import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRevisionRecordSet
import vdi.core.db.cache.model.DatasetShare
import vdi.model.data.*
import vdi.service.rest.generated.model.DatasetDetails
import vdi.service.rest.generated.model.DatasetDetailsImpl
import vdi.service.rest.model.UserDetails
import vdi.service.rest.server.outputs.DatasetContact
import vdi.service.rest.server.outputs.DatasetDependency
import vdi.service.rest.server.outputs.DatasetFundingAward
import vdi.service.rest.server.outputs.DatasetOrganism
import vdi.service.rest.server.outputs.DatasetPublication
import vdi.service.rest.server.outputs.StudyCharacteristics

internal fun DatasetDetails(
  datasetID: DatasetID,
  meta: DatasetMetadata,
  importStatus: DatasetImportStatus,
  importMessages: List<String>,
  shares: List<DatasetShare>,
  installs: Map<InstallTargetID, InstallStatuses>,
  userInfo: Map<UserID, UserDetails>
): DatasetDetails =
  DatasetDetailsImpl().also {
    it.datasetId            = datasetID.toString()
    it.owner                = DatasetOwner(userInfo[meta.owner]!!)
    it.type                 = DatasetTypeOutput(meta.type)
    it.name                 = meta.name
    it.summary              = meta.summary
    it.description          = meta.description
    it.visibility           = DatasetVisibility(meta.visibility)
    it.installTargets       = meta.installTargets.toList()
    it.origin               = meta.origin
    it.dependencies         = meta.dependencies.map(::DatasetDependency)
    it.publications         = meta.publications.map(::DatasetPublication)
    it.contacts             = meta.contacts.map(::DatasetContact)
    it.projectName          = meta.projectName
    it.programName          = meta.programName
    it.relatedStudies       = meta.relatedStudies.map(::RelatedStudy)
    it.experimentalOrganism = meta.experimentalOrganism?.let(::DatasetOrganism)
    it.hostOrganism         = meta.hostOrganism?.let(::DatasetOrganism)
    it.studyCharacteristics = meta.studyCharacteristics?.let(::StudyCharacteristics)
    it.externalIdentifiers  = meta.externalIdentifiers?.let(::ExternalIdentifiers)
    it.funding              = meta.funding.map(::DatasetFundingAward)
    it.created              = meta.created
    it.sourceUrl            = meta.sourceURL?.toString()
    it.revisionHistory      = meta.revisionHistory?.let(::RevisionHistory)
    it.importMessages       = importMessages
    it.shares               = shares.map { ShareOffer(userInfo[it.recipientID]!!, it.offerStatus!!) }
    it.status               = DatasetStatusInfo(importStatus, installs)
  }

internal fun DatasetDetails(
  dataset: DatasetRecord,
  revisions: DatasetRevisionHistory,
  shares: List<DatasetShare>,
  importMessages: List<String>,
  userInfo: Map<UserID, UserDetails>,
): DatasetDetails =
  DatasetDetailsImpl().also {
    it.datasetId            = dataset.datasetID.toString()
    it.owner                = DatasetOwner(userInfo[dataset.ownerID]!!)
    it.type                 = DatasetTypeOutput(dataset.type)
    it.name                 = dataset.name
    it.summary              = dataset.summary
    it.description          = dataset.description
    it.visibility           = DatasetVisibility(dataset.visibility)
    it.installTargets       = dataset.projects
    it.origin               = dataset.origin
    it.dependencies         = emptyList()
    it.publications         = TODO("should there be an index here in the cache db for linking datasets?")
    it.contacts             = emptyList()
    it.projectName          = null
    it.programName          = null
    it.relatedStudies       = TODO("should this be indexed in the cache db?")
    it.experimentalOrganism = null
    it.hostOrganism         = null
    it.studyCharacteristics = null
    it.externalIdentifiers  = null
    it.funding              = emptyList()
    it.created              = dataset.created
    it.sourceUrl            = dataset.sourceURL
    it.revisionHistory      = revisions?.let(::RevisionHistory)
    it.importMessages       = importMessages,
    it.shares               = shares.map { ShareOffer(userInfo[it.recipientID]!!, it.offerStatus!!) }
    it.status               = DatasetStatusInfo(dataset.importStatus, emptyMap())
  }

