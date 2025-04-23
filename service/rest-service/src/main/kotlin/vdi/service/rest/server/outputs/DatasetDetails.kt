package vdi.service.rest.server.outputs

import vdi.service.generated.model.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.time.OffsetDateTime

@Suppress("DuplicatedCode") // overlap in separate API types
internal fun DatasetDetails(
  datasetID: DatasetID,
  owner: vdi.service.rest.generated.model.DatasetOwner,
  datasetType: vdi.service.rest.generated.model.DatasetTypeResponseBody,
  name: String,
  origin: String,
  projectIDs: List<String>,
  visibility: vdi.service.rest.generated.model.DatasetVisibility,
  status: vdi.service.rest.generated.model.DatasetStatusInfo,
  created: OffsetDateTime,
  dependencies: List<DatasetDependencyResponseBody>,
  shortName: String? = null,
  shortAttribution: String? = null,
  category: String? = null,
  summary: String? = null,
  description: String? = null,
  sourceURL: String? = null,
  importMessages: List<String>? = null,
  shares: List<vdi.service.rest.generated.model.ShareOffer>? = null,
  publications: List<vdi.service.rest.generated.model.DatasetPublication>? = null,
  hyperlinks: List<vdi.service.rest.generated.model.DatasetHyperlink>? = null,
  organisms: List<String>? = null,
  contacts: List<vdi.service.rest.generated.model.DatasetContact>? = null,
  originalID: DatasetID? = null,
  revisionHistory: List<vdi.service.rest.generated.model.DatasetRevision>? = null,
): vdi.service.rest.generated.model.DatasetDetails =
  vdi.service.rest.generated.model.DatasetDetailsImpl().also {
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

