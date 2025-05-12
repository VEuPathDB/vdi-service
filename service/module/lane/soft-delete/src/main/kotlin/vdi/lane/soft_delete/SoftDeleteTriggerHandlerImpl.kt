package vdi.lane.soft_delete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.util.or
import vdi.lib.async.WorkerPool
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.AppDatabaseRegistry
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.app.withTransaction
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.db.cache.withTransaction
import vdi.lib.logging.logger
import vdi.lib.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.lib.plugin.client.response.uni.UninstallResponseType
import vdi.lib.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers

internal class SoftDeleteTriggerHandlerImpl(
  private val config: SoftDeleteTriggerHandlerConfig,
  abortCB: AbortCB
)
  : SoftDeleteTriggerHandler
  , AbstractVDIModule("soft-delete-trigger-handler", abortCB, logger<SoftDeleteTriggerHandler>())
{
  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaConfig)
    val wp = WorkerPool("soft-delete-workers", config.jobQueueSize, config.workerCount) {
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
      .labels(internalDBRecord.typeName.toString(), internalDBRecord.typeVersion)
      .startTimer()

    // Grab a plugin handler instance for this dataset type.
    val handler = PluginHandlers[internalDBRecord.typeName, internalDBRecord.typeVersion] or {
      log.error("no plugin handler found for dataset {}/{} type {}:{}", userID, datasetID, internalDBRecord.typeName, internalDBRecord.typeVersion)
      return
    }

    // Iterate through the install targets for the dataset and attempt an
    // uninstall on each.
    internalDBRecord.projects.forEach { projectID ->
      if (!handler.appliesToProject(projectID)) {
        log.warn("type handler for dataset type {}:{} does not apply to project {} (dataset {}/{})", internalDBRecord.typeName, internalDBRecord.typeVersion, projectID, userID, datasetID)
        return@forEach
      }

      if (!AppDatabaseRegistry.contains(projectID, internalDBRecord.typeName)) {
        log.warn("dataset {}/{} cannot be uninstalled from target project {} as the project is disabled", userID, datasetID, projectID)
        return@forEach
      }

      if (datasetShouldBeUninstalled(userID, datasetID, projectID, internalDBRecord.typeName)) {
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

  private suspend fun tryUninstallDataset(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    handler: PluginHandler,
    record: DatasetRecord
  ) {
    try {
      uninstallDataset(userID, datasetID, projectID, handler, record)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.uninstall(handler.displayName, projectID, userID, datasetID, cause = e)
    }
  }

  private suspend fun uninstallDataset(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    handler: PluginHandler,
    record: DatasetRecord
  ) {
    appDB.withTransaction(projectID, record.typeName) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedNotUninstalled) }

    val response = try {
      handler.client.postUninstall(datasetID, projectID, VDIDatasetType(record.typeName, record.typeVersion))
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.displayName, projectID, userID, datasetID, cause = e)
    }

    Metrics.Uninstall.count.labels(record.typeName.toString(), record.typeVersion, response.responseCode.toString()).inc()

    when (response.type) {
      UninstallResponseType.Success
      -> handleSuccessResponse(handler, userID, datasetID, projectID)

      UninstallResponseType.BadRequest
      -> handleBadRequestResponse(handler, userID, datasetID, projectID, response as UninstallBadRequestResponse)

      UninstallResponseType.UnexpectedError
      -> handleUnexpectedErrorResponse(handler, userID, datasetID, projectID, response as UninstallUnexpectedErrorResponse)
    }
  }

  private fun datasetShouldBeUninstalled(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    dataType: DataType,
  ): Boolean {
    val dataset = appDB.accessor(projectID, dataType)!!.selectDataset(datasetID)

    if (dataset == null) {
      log.warn("dataset {}/{} does not appear in target project {}, cannot run uninstall", userID, datasetID, projectID)
      return false
    }

    if (dataset.deletionState == DeleteFlag.DeletedAndUninstalled) {
      log.info("dataset {}/{} has already been successfully uninstalled from target project {}", userID, datasetID, projectID)
      return false
    }

    return true
  }

  private fun handleSuccessResponse(handler: PluginHandler, userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info(
      "dataset handler server reports dataset {}/{} was successfully uninstalled from project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName
    )

    appDB.withTransaction(projectID, handler.type) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedAndUninstalled) }
  }

  private fun handleBadRequestResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    res: UninstallBadRequestResponse
  ) {
    log.error(
      "dataset handler server reports 400 error for uninstall on dataset {}/{} for project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName,
    )

    throw PluginException.uninstall(handler.displayName, projectID, userID, datasetID, res.message)
  }

  private fun handleUnexpectedErrorResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    res: UninstallUnexpectedErrorResponse
  ) {
    log.error(
      "dataset handler server reports 500 for uninstall on dataset {}/{} for project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      res.message,
    )

    throw PluginException.uninstall(handler.displayName, projectID, userID, datasetID, res.message)
  }
}
