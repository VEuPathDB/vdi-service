package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import java.time.OffsetDateTime
import vdi.lib.db.app.model.InstallStatuses
import vdi.lib.db.cache.model.DatasetFileSummary
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.*
import vdi.service.rest.model.UserDetails
import vdi.service.rest.util.defaultZone

@Suppress("DuplicatedCode") // overlap in separate API type fields
internal fun DatasetListEntry(
  datasetID: DatasetID,
  owner: DatasetOwner,
  datasetType: DatasetTypeOutput,
  visibility: DatasetVisibility,
  name: String,
  origin: String,
  projectIDs: List<ProjectID>,
  status: DatasetStatusInfo,
  shares: List<DatasetListShareUser>,
  fileCount: Int,
  fileSizeTotal: Long,
  created: OffsetDateTime,
  shortName: String?,
  shortAttribution: String?,
  category: String?,
  summary: String?,
  description: String?,
  sourceURL: String?,
  originalID: DatasetID?,
): DatasetListEntry =
  vdi.service.rest.generated.model.DatasetListEntryImpl().also {
    it.datasetId = datasetID.toString()
    it.owner = owner
    it.datasetType = datasetType
    it.visibility = visibility
    it.name = name
    it.origin = origin
    it.projectIds = projectIDs
    it.status = status
    it.shares = shares
    it.fileCount = fileCount
    it.fileSizeTotal = fileSizeTotal
    it.created = created
    it.shortName = shortName
    it.shortAttribution = shortAttribution
    it.category = category
    it.summary = summary
    it.description = description
    it.sourceUrl = sourceURL
    it.originalId = originalID?.toString()
  }

internal fun DatasetRecord.toExternal(
  owner: UserDetails,
  statuses: Map<ProjectID, InstallStatuses>?,
  fileInfo: DatasetFileSummary?,
  shares: List<DatasetListShareUser>?,
): DatasetListEntry {
  val typeDisplayName = PluginRegistry[typeName, typeVersion]?.displayName
    ?: throw IllegalStateException("plugin missing: ${typeName}:${typeVersion}")

  return DatasetListEntry(
    datasetID        = datasetID,
    owner            = DatasetOwner(owner),
    datasetType      = DatasetTypeOutput(this, typeDisplayName),
    visibility       = DatasetVisibility(visibility),
    name             = name,
    origin           = origin,
    projectIDs       = projects,
    status           = DatasetStatusInfo(importStatus, statuses ?: emptyMap()),
    shares           = shares ?: emptyList(),
    fileCount        = fileInfo?.count?.toInt() ?: 0,
    fileSizeTotal    = fileInfo?.size?.toLong() ?: 0,
    created          = created.defaultZone(),
    shortName        = shortName,
    shortAttribution = shortAttribution,
    category         = category,
    summary          = summary,
    description      = description,
    sourceURL        = sourceURL,
    originalID       = originalID
  )
}
