package vdi.lib.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID


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
  deleteDatasetProjects(datasetID)
  deleteDatasetShareOffers(datasetID)
  deleteDatasetShareReceipts(datasetID)
  deleteImportControl(datasetID)
  deleteImportMessages(datasetID)
  deleteSyncControl(datasetID)

  if (!retainRevisionHistory)
    deleteRevisions(CacheDB().selectOriginalDatasetID(datasetID))

  deleteDataset(datasetID)
}
