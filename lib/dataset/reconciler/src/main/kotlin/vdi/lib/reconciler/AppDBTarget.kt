package vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.purgeDatasetControlTables
import vdi.lib.db.app.withTransaction
import vdi.lib.db.model.ReconcilerTargetRecord
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.lib.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.lib.plugin.mapping.PluginHandlers

internal class AppDBTarget(override val name: String, private val projectID: String) : ReconcilerTarget {
  private val appDB = AppDB()

  override val type = ReconcilerTargetType.Install

  override fun streamSortedSyncControlRecords(): CloseableIterator<ReconcilerTargetRecord> =
    CloseableMultiIterator(projectID)

  override suspend fun deleteDataset(dataset: ReconcilerTargetRecord) {
    if (!PluginHandlers.contains(dataset.type.name, dataset.type.version)) {
      throw UnsupportedTypeException(
        "Unable to delete unknown dataset type ${dataset.type} from target database " +
          "for project $projectID"
      )
    }

    val handler = PluginHandlers[dataset.type.name, dataset.type.version]!!

    val res = try {
      handler.client.postUninstall(dataset.datasetID, projectID, dataset.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.displayName, projectID, dataset.ownerID, dataset.datasetID, cause = e)
    }

    if (!res.isSuccessResponse)
      when (res) {
        is UninstallBadRequestResponse ->
          throw PluginException.uninstall(handler.displayName, projectID, dataset.ownerID, dataset.datasetID, res.message)

        is UninstallUnexpectedErrorResponse ->
          throw PluginException.uninstall(handler.displayName, projectID, dataset.ownerID, dataset.datasetID, res.message)

        else ->
          throw PluginException.uninstall(handler.displayName, projectID, dataset.ownerID, dataset.datasetID)
      }

      appDB.withTransaction(projectID, dataset.type.name) { it.purgeDatasetControlTables(dataset.datasetID) }
  }
}

