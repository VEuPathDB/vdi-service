package vdi.core.reconciler

import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.purgeDatasetControlTables
import vdi.core.db.app.withTransaction
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.ScriptErrorResponse
import vdi.core.plugin.client.response.ServerErrorResponse
import vdi.core.plugin.client.response.isSuccessResponse
import vdi.core.plugin.mapping.PluginHandlers
import vdi.model.EventID

internal class AppDBTarget(override val name: String, private val projectID: String): ReconcilerTarget {
  private val appDB = AppDB()

  override val type = ReconcilerTargetType.Install

  override fun streamSortedSyncControlRecords(): CloseableIterator<ReconcilerTargetRecord> =
    CloseableMultiIterator(projectID)

  override suspend fun deleteDataset(eventID: EventID, dataset: ReconcilerTargetRecord) {
    if (!PluginHandlers.contains(dataset.type)) {
      throw UnsupportedTypeException(
        "Unable to delete unknown dataset type ${dataset.type} from target database " +
          "for project $projectID"
      )
    }

    val handler = PluginHandlers[dataset.type]!!

    appDB.withTransaction(projectID, dataset.type) {
      it.purgeDatasetControlTables(dataset.datasetID, DeleteFlag.DeletedNotUninstalled)
    }

    val res = try {
      handler.client.postUninstall(eventID, dataset.datasetID, projectID, dataset.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, cause = e)
    }

    if (!res.isSuccessResponse)
      when (res) {
        is ScriptErrorResponse -> throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, res.message)
        is ServerErrorResponse -> throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, res.message)
        else -> throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID)
      }

      appDB.withTransaction(projectID, dataset.type) {
        it.updateDatasetDeletedFlag(dataset.datasetID, DeleteFlag.DeletedAndUninstalled)
      }
  }
}

