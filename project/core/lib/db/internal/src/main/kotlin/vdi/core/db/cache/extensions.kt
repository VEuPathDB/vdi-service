package vdi.core.db.cache

import vdi.model.data.DatasetID


inline fun <T> CacheDB.withTransaction(fn: (CacheDBTransaction) -> T) =
  openTransaction().use { t ->
    try {
      fn(t).also { t.commit() }
    } catch (e: Throwable) {
      t.rollback()
      throw e
    }
  }

fun CacheDBTransaction.purgeDataset(datasetID: DatasetID, retainRevisionHistory: Boolean) {
  deleteInstallFiles(datasetID)
  deleteUploadFiles(datasetID)
  deleteDatasetMetadata(datasetID)
  deleteInstallTargetLinks(datasetID)
  deleteShareOffers(datasetID)
  deleteShareReceipts(datasetID)
  deleteImportControl(datasetID)
  deleteImportMessages(datasetID)
  deleteSyncControl(datasetID)

  if (!retainRevisionHistory)
    deleteRevisions(CacheDB().selectOriginalDatasetID(datasetID))

  deleteDataset(datasetID)
}
