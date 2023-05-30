package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator

interface ReconcilerTarget {
    val name: String

    fun streamSortedSyncControlRecords(): CloseableIterator<VDISyncControlRecord>

    fun deleteDataset(datasetID: DatasetID)
}