package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.InternalDatasetDetails
import org.veupathdb.service.vdi.generated.model.InternalDatasetDetailsImpl
import org.veupathdb.service.vdi.generated.model.SyncControlRecordImpl
import vdi.component.db.cache.model.AdminDatasetDetailsRecord

internal fun InternalDatasetDetails(record: AdminDatasetDetailsRecord): InternalDatasetDetails =
  InternalDatasetDetailsImpl().also {
    it.datasetType = DatasetTypeInfo(record.typeName, record.typeVersion)
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
    it.projectIds = record.projectIDs
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
