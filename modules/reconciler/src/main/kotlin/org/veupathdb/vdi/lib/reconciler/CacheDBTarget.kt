package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.app.AppDBAccessor
import org.veupathdb.vdi.lib.db.app.AppDBTransaction
import org.veupathdb.vdi.lib.db.cache.CacheDB

class CacheDBTarget(override val name: String) : ReconcilerTarget {

    override fun streamSortedSyncControlRecords(): CloseableIterator<VDISyncControlRecord> {
        return CacheDB.selectAllSyncControlRecords()
    }

    override fun deleteDataset(datasetID: DatasetID) {

    }
}