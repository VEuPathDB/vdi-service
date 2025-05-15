package vdi.service.rest.server.outputs

import vdi.lib.db.cache.model.AdminDatasetDetailsRecord
import vdi.service.rest.generated.model.InternalDatasetDetails

internal fun InternalDatasetDetails(record: AdminDatasetDetailsRecord): InternalDatasetDetails =
  vdi.service.rest.generated.model.InternalDatasetDetailsImpl().also {
    it.datasetType = DatasetTypeOutput(record.typeName, record.typeVersion)
    it.owner = record.ownerID.toLong()
    it.isDeleted = record.isDeleted
    it.origin = record.origin
    it.created = record.created
    it.inserted = record.inserted
    it.name = record.name
    it.shortName = record.shortName
    it.shortAttribution = record.shortAttribution
    it.category = record.category
    it.summary = record.summary
    it.description = record.description
    it.visibility = DatasetVisibility(record.visibility)
    it.sourceUrl = record.sourceURL
    it.projectIds = record.projects
    it.status = record.importStatus.toString()

    record.syncControl?.let { queriedSyncControl ->
      it.syncControl = vdi.service.rest.generated.model.SyncControlRecordImpl().also { syncControl ->
        syncControl.dataUpdateTime = queriedSyncControl.dataUpdated
        syncControl.metaUpdateTime = queriedSyncControl.metaUpdated
        syncControl.sharesUpdateTime = queriedSyncControl.sharesUpdated
      }
    }

    it.uploadFiles = record.uploadFiles
    it.installFiles = record.installFiles
    it.importMessages = record.messages
  }
