package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetFileSummary
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.InstallTargetID
import vdi.service.rest.conversion.*
import vdi.service.rest.generated.model.DatasetListEntry
import vdi.service.rest.generated.model.DatasetListEntryImpl
import vdi.service.rest.generated.model.DatasetListShareUser
import vdi.service.rest.model.UserDetails
import vdi.service.rest.util.defaultZone

internal fun DatasetRecord.toExternal(
  owner:    UserDetails,
  installs: Map<InstallTargetID, InstallStatuses?>?,
  fileInfo: DatasetFileSummary?,
  shares:   List<DatasetListShareUser>?,
): DatasetListEntry {
  return DatasetListEntryImpl().also { out ->
    out.datasetId      = datasetID.toString()
    out.owner          = DatasetOwner(owner)
    out.type           = DatasetTypeOutput(this, PluginRegistry.require(type).category)
    out.visibility     = DatasetVisibility(visibility, -1)
    out.name           = name
    out.origin         = origin
    out.installTargets = projects
    out.status         = DatasetStatusInfo(
      DatasetUploadStatusInfo(uploadStatus, null),
      importStatus?.let { DatasetImportStatusInfo(it, null) },
      installs
        ?.asSequence()
        ?.filter { (_, v) -> v?.meta != null }
        ?.map(::DatasetInstallStatusListEntry)
        ?.toList()
        ?.takeUnless(List<*>::isEmpty),
    )
    out.shares         = shares
    out.fileCount      = fileInfo?.count?.toInt() ?: 0
    out.fileSizeTotal  = fileInfo?.size?.toLong() ?: 0
    out.created        = created.defaultZone()
    out.summary        = summary
    out.description    = description
    out.originalId     = originalID?.toString()
  }
}
