@file:JvmName("DatasetDetailsOutputAdaptor")
package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetShare
import vdi.core.db.cache.model.RelatedDataset
import vdi.model.DatasetUploadStatus
import vdi.model.meta.*
import vdi.model.meta.DatasetContact
import vdi.model.meta.DatasetDependency
import vdi.model.meta.DatasetFundingAward
import vdi.model.meta.DatasetHyperlink
import vdi.model.meta.DatasetOrganism
import vdi.model.meta.DatasetPublication
import vdi.model.meta.LinkedDataset
import vdi.model.misc.UploadErrorReport
import vdi.service.rest.conversion.DatasetImportStatusInfo
import vdi.service.rest.conversion.DatasetInstallStatusListEntry
import vdi.service.rest.conversion.DatasetOwner
import vdi.service.rest.conversion.DatasetStatusInfo
import vdi.service.rest.conversion.DatasetUploadStatusInfo
import vdi.service.rest.generated.model.*
import vdi.service.rest.model.UserDetails
import vdi.service.rest.server.conversion.BioprojectIDReference
import vdi.service.rest.server.conversion.DatasetCharacteristics
import vdi.service.rest.server.conversion.DOIReference
import vdi.service.rest.generated.model.DatasetContact as APIContact
import vdi.service.rest.generated.model.DatasetFundingAward as APIFunding
import vdi.service.rest.generated.model.DatasetHyperlink as APIHyperlink
import vdi.service.rest.generated.model.DatasetOrganism as APIOrganism
import vdi.service.rest.generated.model.DatasetPublication as APIPublication
import vdi.service.rest.generated.model.ExternalIdentifiers as APIIdentifiers
import vdi.service.rest.generated.model.LinkedDataset as APILinkedDataset

/**
 * API type conversion for use when the dataset metadata is available from the
 * object store.
 */
internal fun DatasetDetails(
  datasetID: DatasetID,
  meta: DatasetMetadata,
  uploadError: UploadErrorReport?,
  importStatus: DatasetImportStatus?,
  importMessages: List<String>,
  shares: List<DatasetShare>,
  installs: Map<InstallTargetID, InstallStatuses?>,
  userInfo: Map<UserID, UserDetails>,
  relatedDatasets: Sequence<RelatedDataset>,
  files: DatasetFileListing,
): DatasetDetails =
  DatasetDetailsImpl()
    .applyMeta(meta)
    .also {
      it.datasetId = datasetID.toString()
      it.owner = DatasetOwner(userInfo[meta.owner]!!)
      it.relatedDatasets = relatedDatasets.map(::RelatedDatasetInfo).toList()
      it.shares = shares.map { (user, offer) -> ShareOffer(userInfo[user]!!, offer!!) }
        .takeUnless(List<*>::isEmpty)
      it.status = DatasetStatusInfo(
        DatasetUploadStatusInfo(
          uploadError?.status ?: DatasetUploadStatus.Success,
          uploadError?.message,
        ),
        importStatus?.let { status ->
          DatasetImportStatusInfo(status, importMessages.takeUnless(Collection<*>::isEmpty))
        },
        installs.asSequence()
          .filter { (_, statuses) -> statuses?.meta != null }
          .map { (installTarget, statuses) ->
            DatasetInstallStatusListEntry(installTarget, statuses)
          }
          .toList()
          .takeUnless(List<*>::isEmpty)
      )
      it.files = files
    }

/**
 * Sets the basic 1-to-1 metadata fields.
 */
@Suppress("NOTHING_TO_INLINE")
private inline fun DatasetDetails.applyMeta(meta: DatasetMetadata) = apply {
  installTargets         = meta.installTargets.toList()
  name                   = meta.name
  summary                = meta.summary
  description            = meta.description
  origin                 = meta.origin
  dependencies           = DatasetDependencies(meta.dependencies)
  publications           = meta.publications.map(::DatasetPublication)
  contacts               = meta.contacts.map(::DatasetContact)
  projectName            = meta.projectName
  programName            = meta.programName
  linkedDatasets         = meta.linkedDatasets.map(::LinkedDataset)
  experimentalOrganism   = meta.experimentalOrganism?.let(::DatasetOrganism)
  hostOrganism           = meta.hostOrganism?.let(::DatasetOrganism)
  datasetCharacteristics = meta.datasetCharacteristics?.let(::DatasetCharacteristics)
  externalIdentifiers    = meta.externalIdentifiers?.let(::ExternalIdentifiers)
  funding                = meta.funding.map(::DatasetFundingAward)
  shortAttribution       = meta.shortAttribution
  type                   = meta.type.toExternal()
  visibility             = DatasetVisibility(meta.visibility, meta.daysForApproval)
  created                = meta.created
  sourceUrl              = meta.sourceURL?.toString()
  revisionHistory        = meta.revisionHistory?.let(::RevisionHistory)
  shortName              = meta.shortName
  daysForApproval        = meta.daysForApproval
  dataDisclaimer         = meta.dataDisclaimer
}

private fun DatasetContact(contact: DatasetContact): APIContact =
  DatasetContactImpl().apply {
    firstName   = contact.firstName
    middleName  = contact.middleName
    lastName    = contact.lastName
    email       = contact.email
    affiliation = contact.affiliation
    country     = contact.country
    isPrimary   = contact.isPrimary
  }

private fun DatasetDependencies(dependencies: Collection<DatasetDependency>) =
  dependencies.map {
    DatasetDependencyImpl().apply {
      resourceIdentifier  = it.identifier
      resourceDisplayName = it.displayName
      resourceVersion     = it.version
    }
  }

private fun DatasetFundingAward(award: DatasetFundingAward): APIFunding =
  DatasetFundingAwardImpl().also {
    it.agency      = award.agency
    it.awardNumber = award.awardNumber
  }

private fun DatasetHyperlink(link: DatasetHyperlink): APIHyperlink =
  DatasetHyperlinkImpl().also {
    it.url         = link.url.toString()
    it.description = link.description
  }

private fun DatasetOrganism(organism: DatasetOrganism): APIOrganism =
  DatasetOrganismImpl().also {
    it.species = organism.species
    it.strain  = organism.strain
  }

private fun DatasetPublication(publication: DatasetPublication): APIPublication =
  DatasetPublicationImpl().also {
    it.identifier = publication.identifier
    it.type       = DatasetPublicationType(publication.type)
    it.citation   = publication.citation
    it.isPrimary  = publication.isPrimary
  }

private fun ExternalIdentifiers(identifiers: ExternalDatasetIdentifiers): APIIdentifiers =
  ExternalIdentifiersImpl().also {
    it.dois          = identifiers.dois.map(::DOIReference)
    it.hyperlinks    = identifiers.hyperlinks.map(::DatasetHyperlink)
    it.bioprojectIds = identifiers.bioprojectIDs.map(::BioprojectIDReference)
  }

private fun ImplicitRelation(other: RelatedDataset): ImplicitRelation =
  when (other.relationType) {
    RelatedDataset.RelationType.ProgramName -> RelationByProgramNameImpl()
    RelatedDataset.RelationType.ProjectName -> RelationByProjectNameImpl()
    RelatedDataset.RelationType.Publication -> RelationByPublicationImpl().apply {
      identifier = other.publication!!.identifier
      type       = DatasetPublicationType(other.publication!!.type)
    }
  }

private fun LinkedDataset(link: LinkedDataset): APILinkedDataset =
  LinkedDatasetImpl().apply {
    datasetUri    = link.datasetURI.toString()
    sharesRecords = link.sharesRecords
  }

private fun RelatedDatasetInfo(other: RelatedDataset): RelatedDatasetInfo =
  RelatedDatasetInfoImpl().apply {
    datasetId = other.datasetID.toString()
    type      = other.datasetType.toExternal()
    name      = other.name
    summary   = other.summary
    created   = other.created
    relatedBy = ImplicitRelation(other)
  }
