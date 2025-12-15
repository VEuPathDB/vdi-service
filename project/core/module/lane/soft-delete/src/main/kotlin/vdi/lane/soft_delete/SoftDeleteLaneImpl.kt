package vdi.lane.soft_delete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.withTransaction
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.util.orElse
import vdi.core.async.WorkerPool
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.cache.withTransaction
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.EmptySuccessResponse
import vdi.core.plugin.client.response.ServiceErrorResponse
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

internal class SoftDeleteLaneImpl(
  private val config: SoftDeleteLaneConfig,
  abortCB: AbortCB
)
  : SoftDeleteLane
  , AbstractVDIModule(abortCB, logger<SoftDeleteLane>())
{
  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaConfig)
    val wp = WorkerPool.create<SoftDeleteLane>(config.jobQueueSize, config.workerCount) {
      Metrics.softDeleteQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.eventKey)
            .map { SoftDeleteContext(it, logger) }
            .onEach { it.logger.info("received uninstall job from source {}", it.eventSource) }
            .forEach { wp.submit { it.tryHandleUninstall() } }
        }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun SoftDeleteContext.tryHandleUninstall() {
    try {
      handleUninstall()
    } catch (e: PluginException) {
      e.log(logger::error)
    } catch (e: Throwable) {
      PluginException.uninstall("N/A", "N/A", ownerID, datasetID, cause = e).log(logger::error)
    }
  }

  private suspend fun SoftDeleteContext.handleUninstall() {
    val internalDBRecord = cacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("received uninstall event for a dataset that does not exist in the internal database")

    // Mark the dataset is deleted in the internal postgres database regardless
    // of whether the uninstalls from the dataset's install targets succeed.
    cacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    val timer = Metrics.Uninstall.duration
      .labels(internalDBRecord.type.name.toString(), internalDBRecord.type.version)
      .startTimer()

    // Grab a plugin handler instance for this dataset type.
    val handler = PluginHandlers[internalDBRecord.type] orElse {
      logger.error("no plugin handler found type {}", internalDBRecord.type)
      return
    }

    // Iterate through the install targets for the dataset and attempt an
    // uninstall on each.
    internalDBRecord.projects.forEach { projectID ->
      if (!handler.appliesToProject(projectID)) {
        logger.warn("type handler for {} does not apply to project {}", internalDBRecord.type, projectID)
        return@forEach
      }

      if (!AppDatabaseRegistry.contains(projectID, internalDBRecord.type)) {
        logger.warn("dataset cannot be uninstalled from target project {}; project is disabled", projectID)
        return@forEach
      }

      if (datasetShouldBeUninstalled(projectID, internalDBRecord.type)) {
        try {
          uninstallDataset(projectID, handler, internalDBRecord)
        } catch (e: PluginException) {
          throw e
        } catch (e: Throwable) {
          throw PluginException.uninstall(handler.name, projectID, ownerID, datasetID, cause = e)
        }
      }
    }

    timer.observeDuration()
  }

  private suspend fun SoftDeleteContext.uninstallDataset(
    installTarget: InstallTargetID,
    handler: PluginHandler,
    record: DatasetRecord
  ) {
    appDB.withTransaction(installTarget, record.type) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedNotUninstalled) }

    val response = try {
      handler.client.postUninstall(eventID, datasetID, installTarget, record.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.name, installTarget, ownerID, datasetID, cause = e)
    }

    Metrics.Uninstall.count.labels(record.type.name.toString(), record.type.version, response.status.code.toString()).inc()

    when (response) {
      is EmptySuccessResponse -> handleSuccessResponse(handler, installTarget)
      is ServiceErrorResponse -> handleUnexpectedErrorResponse(handler, installTarget, response)
    }
  }

  private fun SoftDeleteContext.datasetShouldBeUninstalled(
    installTarget: InstallTargetID,
    record:        DatasetRecord,
  ): Boolean {
    val dataset = appDB.accessor(installTarget, record.type)!!.selectDataset(datasetID)

    if (dataset == null) {
      // If the import status was not complete, then we didn't actually expect
      // the dataset to be installed in the first place, this is more of a worst
      // case safety check.
      if (record.importStatus == DatasetImportStatus.Complete)
        logger.warn("dataset does not appear in target project {}, cannot run uninstall", installTarget)
      return false
    }

    if (dataset.deletionState == DeleteFlag.DeletedAndUninstalled) {
      logger.info("dataset has already been successfully uninstalled from target project {}", installTarget)
      return false
    }

    return true
  }

  private fun SoftDeleteContext.handleSuccessResponse(handler: PluginHandler, installTarget: InstallTargetID) {
    logger.info("dataset successfully uninstalled from project {} via plugin {}", installTarget, handler.name)
    appDB.withTransaction(installTarget, handler.type) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedAndUninstalled) }
  }

  private fun SoftDeleteContext.handleUnexpectedErrorResponse(
    handler: PluginHandler,
    installTarget: InstallTargetID,
    res: ServiceErrorResponse
  ) {
    logger.error("unhandled error when uninstalling dataset from project {} via plugin {}", installTarget, res.message)
    throw PluginException.uninstall(handler.name, installTarget, ownerID, datasetID, res.message)
  }
}
