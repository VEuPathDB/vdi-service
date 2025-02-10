package vdi.lib.reconciler

import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.withTransaction
import vdi.component.plugin.client.PluginException
import vdi.component.plugin.client.PluginRequestException
import vdi.component.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.component.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.component.plugin.mapping.PluginHandlers

internal class AppDBTarget(override val name: String, private val projectID: String) : ReconcilerTarget {
  private val appDB = AppDB()

  override val type = ReconcilerTargetType.Install

  override fun streamSortedSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> =
    CloseableMultiIterator(projectID)

  override suspend fun deleteDataset(dataset: VDIReconcilerTargetRecord) {
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

      appDB.withTransaction(projectID, dataset.type.name) {
        it.deleteDatasetVisibilities(dataset.datasetID)
        it.deleteDatasetProjectLinks(dataset.datasetID)
        it.deleteSyncControl(dataset.datasetID)
        it.deleteInstallMessages(dataset.datasetID)
        it.deleteDatasetContacts(dataset.datasetID)
        it.deleteDatasetHyperlinks(dataset.datasetID)
        it.deleteDatasetPublications(dataset.datasetID)
        it.deleteDatasetOrganisms(dataset.datasetID)
        it.deleteDatasetDependencies(dataset.datasetID)
        it.deleteDatasetMeta(dataset.datasetID)
        it.deleteDataset(dataset.datasetID)
      }
  }
}

private class CloseableMultiIterator(private val projectID: ProjectID) : CloseableIterator<VDIReconcilerTargetRecord> {
  private val dataTypeIterator = AppDatabaseRegistry[projectID]!!.keys().iterator()
  private var current: CloseableIterator<VDIReconcilerTargetRecord>? = null

  override fun hasNext() =
    current?.let { it.hasNext() || dataTypeIterator.hasNext() }
      ?: dataTypeIterator.hasNext()

  override fun next(): VDIReconcilerTargetRecord {
    // iterate until we have a record stream with at least one record in it.
    while (current?.hasNext() != true) {
      // close the previous stream (if one exists)
      current?.close()

      // ensure there is another type to move to for the current project
      if (!dataTypeIterator.hasNext())
        throw NoSuchElementException()

      // attempt to get a stream for the project/type pairing
      current = AppDB().accessor(projectID, dataTypeIterator.next())?.streamAllSyncControlRecords()
    }

    return current!!.next()
  }

  override fun close() {
    current?.close()
  }
}
