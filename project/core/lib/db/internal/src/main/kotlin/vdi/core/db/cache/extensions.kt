@file:JvmName("CacheDBExtensions")
package vdi.core.db.cache

import vdi.model.meta.DatasetID

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