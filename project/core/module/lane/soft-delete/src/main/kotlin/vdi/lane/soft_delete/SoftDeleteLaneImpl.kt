package vdi.lane.soft_delete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.withTransaction
import vdi.core.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.util.orElse
import vdi.lib.async.WorkerPool
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.db.cache.withTransaction
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.lib.plugin.client.response.uni.UninstallResponseType
import vdi.lib.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

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
    val wp = WorkerPool("soft-del", config.jobQueueSize, config.workerCount) {
      Metrics.softDeleteQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.eventKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received uninstall job for dataset $userID/$datasetID from source $source")
              wp.submit { tryHandleUninstall(userID, datasetID) }
            }
        }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun tryHandleUninstall(userID: UserID, datasetID: DatasetID) {
    try {
      handleUninstall(userID, datasetID)
    } catch (e: PluginException) {
      e.log(log::error)
    } catch (e: Throwable) {
      PluginException.uninstall("N/A", "N/A", userID, datasetID, cause = e).log(log::error)
    }
  }

  private suspend fun handleUninstall(userID: UserID, datasetID: DatasetID) {
    val internalDBRecord = cacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("received uninstall event for a dataset that does not exist in the internal database")

    // Mark the dataset is deleted in the internal postgres database regardless
    // of whether the uninstalls from the dataset's install targets succeed.
    cacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    val timer = Metrics.Uninstall.duration
      .labels(internalDBRecord.type.name.toString(), internalDBRecord.type.version)
      .startTimer()

    // Grab a plugin handler instance for this dataset type.
    val handler = PluginHandlers[internalDBRecord.type.name, internalDBRecord.type.version] orElse {
      log.error("no plugin handler found for dataset {}/{} type {}:{}", userID, datasetID, internalDBRecord.type.name, internalDBRecord.type.version)
      return
    }

    // Iterate through the install targets for the dataset and attempt an
    // uninstall on each.
    internalDBRecord.projects.forEach { projectID ->
      if (!handler.appliesToProject(projectID)) {
        log.warn("type handler for dataset type {}:{} does not apply to project {} (dataset {}/{})", internalDBRecord.type.name, internalDBRecord.type.version, projectID, userID, datasetID)
        return@forEach
      }

      if (!AppDatabaseRegistry.contains(projectID, internalDBRecord.type)) {
        log.warn("dataset {}/{} cannot be uninstalled from target project {} as the project is disabled", userID, datasetID, projectID)
        return@forEach
      }

      if (datasetShouldBeUninstalled(userID, datasetID, projectID, internalDBRecord.type)) {
        try {
          uninstallDataset(userID, datasetID, projectID, handler, internalDBRecord)
        } catch (e: PluginException) {
          throw e
        } catch (e: Throwable) {
          throw PluginException.uninstall(handler.displayName, projectID, userID, datasetID, cause = e)
        }
      }
    }

    timer.observeDuration()
  }

  private suspend fun uninstallDataset(
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    handler: PluginHandler,
    record: DatasetRecord
  ) {
    appDB.withTransaction(installTarget, record.type) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedNotUninstalled) }

    val response = try {
      handler.client.postUninstall(datasetID, installTarget, record.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.displayName, installTarget, userID, datasetID, cause = e)
    }

    Metrics.Uninstall.count.labels(record.type.name.toString(), record.type.version, response.responseCode.toString()).inc()

    when (response.type) {
      UninstallResponseType.Success
      -> handleSuccessResponse(handler, userID, datasetID, installTarget)

      UninstallResponseType.BadRequest
      -> handleBadRequestResponse(handler, userID, datasetID, installTarget, response as UninstallBadRequestResponse)

      UninstallResponseType.UnexpectedError
      -> handleUnexpectedErrorResponse(handler, userID, datasetID, installTarget, response as UninstallUnexpectedErrorResponse)
    }
  }

  private fun datasetShouldBeUninstalled(
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    dataType: DatasetType,
  ): Boolean {
    val dataset = appDB.accessor(installTarget, dataType)!!.selectDataset(datasetID)

    if (dataset == null) {
      log.warn("dataset {}/{} does not appear in target project {}, cannot run uninstall", userID, datasetID, installTarget)
      return false
    }

    if (dataset.deletionState == DeleteFlag.DeletedAndUninstalled) {
      log.info("dataset {}/{} has already been successfully uninstalled from target project {}", userID, datasetID, installTarget)
      return false
    }

    return true
  }

  private fun handleSuccessResponse(handler: PluginHandler, userID: UserID, datasetID: DatasetID, installTarget: InstallTargetID) {
    log.info(
      "dataset handler server reports dataset {}/{} was successfully uninstalled from project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      handler.displayName
    )

    appDB.withTransaction(installTarget, handler.type) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedAndUninstalled) }
  }

  private fun handleBadRequestResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    res: UninstallBadRequestResponse
  ) {
    log.error(
      "dataset handler server reports 400 error for uninstall on dataset {}/{} for project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      handler.displayName,
    )

    throw PluginException.uninstall(handler.displayName, installTarget, userID, datasetID, res.message)
  }

  private fun handleUnexpectedErrorResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    res: UninstallUnexpectedErrorResponse
  ) {
    log.error(
      "dataset handler server reports 500 for uninstall on dataset {}/{} for project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      res.message,
    )

    throw PluginException.uninstall(handler.displayName, installTarget, userID, datasetID, res.message)
  }
}
