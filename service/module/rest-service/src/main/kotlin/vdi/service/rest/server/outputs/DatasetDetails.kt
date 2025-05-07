package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.time.OffsetDateTime
import vdi.service.rest.generated.model.*

@Suppress("DuplicatedCode") // overlap in separate API types
internal fun DatasetDetails(
  datasetID: DatasetID,
  owner: DatasetOwner,
  datasetType: DatasetTypeOutput,
  name: String,
  origin: String,
  projectIDs: List<String>,
  visibility: DatasetVisibility,
  status: DatasetStatusInfo,
  created: OffsetDateTime,
  dependencies: List<DatasetDependency>,
  shortName: String? = null,
  shortAttribution: String? = null,
  category: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  importMessages: List<String>? = null,
  shares: List<ShareOffer>? = null,
  publications: List<DatasetPublication>? = null,
  hyperlinks: List<DatasetHyperlink>? = null,
  organisms: List<String>? = null,
  contacts: List<DatasetContact>? = null,
  originalID: DatasetID? = null,
  revisionHistory: List<DatasetRevision>? = null,
): DatasetDetails =
  DatasetDetailsImpl().also {
    it.datasetId = datasetID.toString()
    it.owner = owner
    it.datasetType = datasetType
    it.name = name
    it.origin = origin
    it.projectIds = projectIDs
    it.visibility = visibility
    it.status = status
    it.created = created
    it.dependencies = dependencies
    it.shortName = shortName
    it.shortAttribution = shortAttribution
    it.category = category
    it.summary = summary
    it.description = description
    it.sourceUrl = sourceURL
    it.importMessages = importMessages
    it.shares = shares
    it.publications = publications
    it.hyperlinks = hyperlinks
    it.organisms = organisms
    it.contacts = contacts
    it.originalId = originalID?.toString()
    it.revisionHistory = revisionHistory
  }

