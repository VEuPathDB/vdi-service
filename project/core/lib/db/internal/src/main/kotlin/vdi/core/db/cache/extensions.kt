@file:JvmName("CacheDBExtensions")
package vdi.core.db.cache

import java.time.OffsetDateTime
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.model.SyncControlRecord
import vdi.model.DatasetUploadStatus
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata

inline fun <T> CacheDB.withTransaction(fn: (CacheDBTransaction) -> T) =
  openTransaction().use { t ->
    try {
      fn(t).also { t.commit() }
    } catch (e: Throwable) {
      t.rollback()
      throw e
    }
  }

/**
 * Removes all records about a dataset from the cache database except for a row
 * in the `datasets` table indicating that the dataset once existed, but is now
 * deleted.
 */
fun CacheDBTransaction.purgeDataset(datasetID: DatasetID) {
  updateDatasetDeleted(datasetID, true)

  deleteDatasetMetadata(datasetID)
  deleteDatasetInstallTargets(datasetID)
  deleteSyncControlRecords(datasetID)
  deleteShareOffers(datasetID)
  deleteShareReceipts(datasetID)
  deleteImportControlRecords(datasetID)
  deleteImportMessages(datasetID)
  deleteUploadFiles(datasetID)
  deleteInstallFiles(datasetID)
  deleteRevisions(selectOriginalDatasetID(datasetID))
  deletePublications(datasetID)
}

fun CacheDBTransaction.initializeDataset(datasetID: DatasetID, meta: DatasetMetadata) {
  // Insert a new dataset record
  tryInsertDataset(DatasetImpl(
    datasetID    = datasetID,
    type         = meta.type,
    ownerID      = meta.owner,
    isDeleted    = false,
    created      = meta.created,
    origin       = meta.origin,
    inserted     = OffsetDateTime.now(),

    // Status fields are ignored by tryInsertDataset
    importStatus = null,
    uploadStatus = DatasetUploadStatus.Running,
  ))

  // insert metadata for the dataset
  tryInsertDatasetMeta(datasetID, meta)

  upsertUploadStatus(datasetID, DatasetUploadStatus.Running)

  // insert project links for the dataset
  tryInsertDatasetProjects(datasetID, meta.installTargets)

  // insert a sync control record for the dataset using an old timestamp
  // that will predate any possible upload timestamp.
  tryInsertSyncControl(SyncControlRecord(
    datasetID     = datasetID,
    sharesUpdated = OriginTimestamp,
    dataUpdated   = OriginTimestamp,
    metaUpdated   = OriginTimestamp
  ))
}