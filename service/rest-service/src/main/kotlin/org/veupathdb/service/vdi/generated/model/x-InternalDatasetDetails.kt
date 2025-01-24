package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.AdminDatasetDetailsRecord

internal fun InternalDatasetDetails(record: AdminDatasetDetailsRecord): InternalDatasetDetails =
  InternalDatasetDetailsImpl().also {
    it.name = record.name
    it.shortName = record.shortName
    it.shortAttribution = record.shortAttribution
    it.category = record.category
    it.created = record.created
    it.inserted = record.inserted
    it.origin = record.origin
    it.projectIds = record.projectIDs
    it.description = record.description
    it.owner = record.ownerID.toLong()
    it.sourceUrl = record.sourceURL
    it.summary = record.summary
    it.status = record.importStatus.toString()
    it.visibility = DatasetVisibility(record.visibility)

    record.syncControl?.let { queriedSyncControl ->
      it.syncControl = SyncControlRecordImpl().also { syncControl ->
        syncControl.dataUpdateTime = queriedSyncControl.dataUpdated
        syncControl.metaUpdateTime = queriedSyncControl.metaUpdated
        syncControl.sharesUpdateTime = queriedSyncControl.sharesUpdated
      }
    }

    it.importMessages = record.messages
    it.installFiles = record.installFiles
    it.uploadFiles = record.uploadFiles
  }
