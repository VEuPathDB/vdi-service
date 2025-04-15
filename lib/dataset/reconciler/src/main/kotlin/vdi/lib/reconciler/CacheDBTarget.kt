package vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.cache.withTransaction

internal class CacheDBTarget : ReconcilerTarget {
  private val cacheDB = vdi.component.db.cache.CacheDB()

  override val name = "cache-db"

  override val type = ReconcilerTargetType.Cache

  override fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
    return cacheDB.selectAllSyncControlRecords()
  }

  override suspend fun deleteDataset(dataset: VDIReconcilerTargetRecord) {
    cacheDB.withTransaction {
      // Delete dataset and all associated rows in a transaction.
      it.deleteInstallFiles(dataset.datasetID)
      it.deleteUploadFiles(dataset.datasetID)
      it.deleteDatasetShareReceipts(dataset.datasetID)
      it.deleteDatasetShareOffers(dataset.datasetID)
      it.deleteDatasetMetadata(dataset.datasetID)
      it.deleteDatasetProjects(dataset.datasetID)
      it.deleteSyncControl(dataset.datasetID)
      it.deleteImportControl(dataset.datasetID)
      it.deleteImportMessages(dataset.datasetID)
      it.deleteDataset(dataset.datasetID)
    }
  }
}