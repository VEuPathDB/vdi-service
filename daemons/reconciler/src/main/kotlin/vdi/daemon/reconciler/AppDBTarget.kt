package vdi.daemon.reconciler

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.app.AppDB
import vdi.component.db.app.withTransaction
import vdi.component.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.component.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.component.plugin.mapping.PluginHandlers

class AppDBTarget(
  override val name: String,
  private val projectID: String
) : ReconcilerTarget {

  private val appDB = AppDB()

  override val type = ReconcilerTargetType.Install

  override fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
    return appDB.accessor(projectID)!!.streamAllSyncControlRecords()
  }

  override suspend fun deleteDataset(datasetType: VDIDatasetType, datasetID: DatasetID) {
    if (!PluginHandlers.contains(datasetType.name, datasetType.version)) {
      throw UnsupportedTypeException(
        "Unable to delete unknown dataset type $datasetType from target database " +
          "for project $projectID"
      )
    }

    val res = PluginHandlers[datasetType.name, datasetType.version]!!.client.postUninstall(datasetID, projectID)

    if (!res.isSuccessResponse)
      when (res) {
        is UninstallBadRequestResponse -> throw Exception("Failed to uninstall dataset $datasetID from project $projectID: ${res.message}")
        is UninstallUnexpectedErrorResponse -> throw Exception("Failed to uninstall dataset $datasetID from project $projectID: ${res.message}")
        else -> throw Exception("Failed to uninstall dataset $datasetID from project $projectID")
      }

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