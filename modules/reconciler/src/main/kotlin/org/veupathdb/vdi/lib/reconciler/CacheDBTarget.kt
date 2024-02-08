package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.cache.CacheDB

class CacheDBTarget : ReconcilerTarget {
  override val name = "cache-db"

  override val type = ReconcilerTargetType.Cache

  override fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
    return CacheDB.selectAllSyncControlRecords()
  }

  override fun deleteDataset(datasetType: VDIDatasetType, datasetID: DatasetID) {
    CacheDB.withTransaction {
      // Delete dataset and all associated rows in a transaction.
      it.deleteInstallFiles(datasetID)
      it.deleteUpdateFiles(datasetID)
      it.deleteDatasetShareReceipts(datasetID)
      it.deleteDatasetShareOffers(datasetID)
      it.deleteDatasetMetadata(datasetID)
      it.deleteDatasetProjects(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteImportControl(datasetID)
      it.deleteDataset(datasetID)
    }
  }
}