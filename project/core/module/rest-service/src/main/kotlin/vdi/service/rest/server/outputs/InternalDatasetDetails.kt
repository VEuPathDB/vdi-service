package vdi.service.rest.server.outputs

import vdi.core.db.cache.model.AdminDatasetDetailsRecord
import vdi.service.rest.generated.model.InternalDatasetDetails
import vdi.service.rest.generated.model.InternalDatasetDetailsImpl
import vdi.service.rest.generated.model.SyncControlRecordImpl

internal fun InternalDatasetDetails(record: AdminDatasetDetailsRecord): InternalDatasetDetails =
  InternalDatasetDetailsImpl().also {
    it.datasetType = record.type.toExternal()
    it.owner = record.ownerID.toLong()
    it.isDeleted = record.isDeleted
    it.origin = record.origin
    it.created = record.created
    it.inserted = record.inserted
    it.name = record.name
    it.shortName = record.shortName
    it.shortAttribution = record.shortAttribution
    it.summary = record.summary
    it.description = record.description
    it.visibility = DatasetVisibility(record.visibility)
    it.sourceUrl = record.sourceURL
    it.installTargets = record.projects
    it.status = record.importStatus.toString()

    record.syncControl?.let { queriedSyncControl ->
      it.syncControl = SyncControlRecordImpl().also { syncControl ->
        syncControl.dataUpdateTime = queriedSyncControl.dataUpdated
        syncControl.metaUpdateTime = queriedSyncControl.metaUpdated
        syncControl.sharesUpdateTime = queriedSyncControl.sharesUpdated
      }
    }

    it.uploadFiles = record.uploadFiles
    it.installFiles = record.installFiles
    it.importMessages = record.messages
  }
