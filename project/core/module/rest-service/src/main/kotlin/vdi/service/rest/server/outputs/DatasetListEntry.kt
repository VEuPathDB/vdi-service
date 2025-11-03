package vdi.service.rest.server.outputs

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import java.time.OffsetDateTime
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetFileSummary
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.plugin.registry.PluginRegistry
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
  installTargets: List<InstallTargetID>,
  status: DatasetStatusInfo,
  shares: List<DatasetListShareUser>,
  fileCount: Int,
  fileSizeTotal: Long,
  created: OffsetDateTime,
  summary: String?,
  description: String?,
  sourceURL: String?,
  originalID: DatasetID?,
): DatasetListEntry =
  DatasetListEntryImpl().also {
    it.datasetId = datasetID.toString()
    it.owner = owner
    it.type = datasetType
    it.visibility = visibility
    it.name = name
    it.origin = origin
    it.installTargets = installTargets
    it.status = status
    it.shares = shares
    it.fileCount = fileCount
    it.fileSizeTotal = fileSizeTotal
    it.created = created
    it.summary = summary
    it.description = description
    it.sourceUrl = sourceURL
    it.originalId = originalID?.toString()
  }

internal fun DatasetRecord.toExternal(
  owner: UserDetails,
  statuses: Map<InstallTargetID, InstallStatuses>?,
  fileInfo: DatasetFileSummary?,
  shares: List<DatasetListShareUser>?,
): DatasetListEntry {
  return DatasetListEntry(
    datasetID        = datasetID,
    owner            = DatasetOwner(owner),
    datasetType      = DatasetTypeOutput(this, PluginRegistry.displayNameFor(type)),
    visibility       = DatasetVisibility(visibility),
    name             = name,
    origin           = origin,
    installTargets   = projects,
    status           = DatasetStatusInfo(importStatus, statuses ?: emptyMap()),
    shares           = shares ?: emptyList(),
    fileCount        = fileInfo?.count?.toInt() ?: 0,
    fileSizeTotal    = fileInfo?.size?.toLong() ?: 0,
    created          = created.defaultZone(),
    summary          = summary,
    description      = description,
    sourceURL        = sourceURL,
    originalID       = originalID
  )
}
