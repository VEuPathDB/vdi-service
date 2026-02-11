package vdi.service.rest.server.outputs

import java.time.OffsetDateTime
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetFileSummary
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID
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
    it.originalId = originalID?.toString()
  }

internal fun DatasetRecord.toExternal(
  owner:    UserDetails,
  installs: Map<InstallTargetID, InstallStatuses?>,
  fileInfo: DatasetFileSummary?,
  shares:   List<DatasetListShareUser>?,
): DatasetListEntry {
  return DatasetListEntry(
    datasetID        = datasetID,
    owner            = DatasetOwner(owner),
    datasetType      = DatasetTypeOutput(this, PluginRegistry.require(type).category),
    visibility       = DatasetVisibility(visibility),
    name             = name,
    origin           = origin,
    installTargets   = projects,
    status           = DatasetStatusInfo(uploadStatus, importStatus, null, installs),
    shares           = shares ?: emptyList(),
    fileCount        = fileInfo?.count?.toInt() ?: 0,
    fileSizeTotal    = fileInfo?.size?.toLong() ?: 0,
    created          = created.defaultZone(),
    summary          = summary,
    description      = description,
    originalID       = originalID,
  )
}
