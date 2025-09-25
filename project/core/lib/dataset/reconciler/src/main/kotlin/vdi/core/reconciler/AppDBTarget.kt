package vdi.core.reconciler

import vdi.util.io.CloseableIterator
import vdi.core.db.app.AppDB
import vdi.core.db.app.purgeDatasetControlTables
import vdi.core.db.app.withTransaction
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.core.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.core.plugin.mapping.PluginHandlers

internal class AppDBTarget(override val name: String, private val projectID: String) : ReconcilerTarget {
  private val appDB = AppDB()

  override val type = ReconcilerTargetType.Install

  override fun streamSortedSyncControlRecords(): CloseableIterator<ReconcilerTargetRecord> =
    CloseableMultiIterator(projectID)

  override suspend fun deleteDataset(dataset: ReconcilerTargetRecord) {
    if (!PluginHandlers.contains(dataset.type)) {
      throw UnsupportedTypeException(
        "Unable to delete unknown dataset type ${dataset.type} from target database " +
          "for project $projectID"
      )
    }

    val handler = PluginHandlers[dataset.type]!!

    val res = try {
      handler.client.postUninstall(dataset.datasetID, projectID, dataset.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, cause = e)
    }

    if (!res.isSuccessResponse)
      when (res) {
        is UninstallBadRequestResponse ->
          throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, res.message)

        is UninstallUnexpectedErrorResponse ->
          throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID, res.message)

        else ->
          throw PluginException.uninstall(handler.name, projectID, dataset.ownerID, dataset.datasetID)
      }

      appDB.withTransaction(projectID, dataset.type) { it.purgeDatasetControlTables(dataset.datasetID) }
  }
}

