package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction

class CacheDBTarget : ReconcilerTarget {
    override val name = "cache-db"

    override fun streamSortedSyncControlRecords(): CloseableIterator<Pair<VDIDatasetType, VDISyncControlRecord>> {
        return CacheDB.selectAllSyncControlRecords()
    }

    override fun deleteDataset(datasetType: VDIDatasetType, datasetID: DatasetID) {
        val transaction: CacheDBTransaction = CacheDB.openTransaction()
        // Delete dataset and all associated rows in a transaction.
        transaction.deleteDatasetShareReceipts(datasetID)
        transaction.deleteDatasetShareOffers(datasetID)
        transaction.deleteDatasetMetadata(datasetID)
        transaction.deleteDatasetProjects(datasetID)
        transaction.deleteDataset(datasetID)
        transaction.deleteSyncControl(datasetID)
        transaction.deleteImportControl(datasetID)
        transaction.commit()
    }
}