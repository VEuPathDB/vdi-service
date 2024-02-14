package org.veupathdb.vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.withTransaction
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.reconciler.exception.UnsupportedTypeException

class AppDBTarget(
    override val name: String,
    private val projectID: String
) : ReconcilerTarget {

    private val appDB = AppDB()

    override val type = ReconcilerTargetType.Install

    override fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
        return appDB.accessor(projectID)!!.streamAllSyncControlRecords()
    }

    override fun deleteDataset(datasetType: VDIDatasetType, datasetID: DatasetID) {
        if (!PluginHandlers.contains(datasetType.name, datasetType.version)) {
            throw UnsupportedTypeException("Unable to delete unknown dataset type $datasetType from target database " +
                    "for project $projectID")
        }
        PluginHandlers[datasetType.name, datasetType.version]!!.client.postUninstall(datasetID, projectID)
        appDB.withTransaction(projectID) {
            it.deleteDatasetVisibilities(datasetID)
            it.deleteDatasetProjectLinks(datasetID)
            it.deleteSyncControl(datasetID)
            it.deleteInstallMessages(datasetID)
            it.deleteDatasetMeta(datasetID)
            it.deleteDataset(datasetID)
        }
    }
}