package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.app.AppDB

class AppDBTarget(
    override val name: String,
    private val projectID: String
) : ReconcilerTarget {

    override fun streamSortedSyncControlRecords(): CloseableIterator<VDISyncControlRecord> {
        return AppDB.accessor(projectID).streamAllSyncControlRecords()
    }

    override fun deleteDataset(datasetID: DatasetID) {
        AppDB.withTransaction(projectID) {
            it.deleteDatasetVisibilities(datasetID)
            it.deleteDatasetProjectLinks(datasetID)
            it.deleteSyncControl(datasetID)
            it.deleteInstallMessages(datasetID)
            it.deleteDataset(datasetID)
        }
    }
}