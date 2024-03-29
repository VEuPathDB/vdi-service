package vdi.lane.delete.soft

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.async.WorkerPool
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.withTransaction
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.withTransaction
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractVDIModule
import vdi.component.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.component.plugin.client.response.uni.UninstallResponseType
import vdi.component.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.component.plugin.mapping.PluginHandlers

internal class SoftDeleteTriggerHandlerImpl(private val config: SoftDeleteTriggerHandlerConfig)
  : SoftDeleteTriggerHandler
  , AbstractVDIModule("soft-delete-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val cacheDB = vdi.component.db.cache.CacheDB()

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.softDeleteTriggerTopic, config.kafkaConsumerConfig)
    val wp = WorkerPool("soft-delete-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.softDeleteQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.softDeleteTriggerMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received uninstall job for dataset $userID/$datasetID from source $source")
              wp.submit { runJob(userID, datasetID) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    kc.close()
    confirmShutdown()
  }

  private suspend fun runJob(userID: UserID, datasetID: DatasetID) {
    val internalDBRecord = cacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("received uninstall event for a dataset that does not exist in the internal database")

    // Mark the dataset is deleted in the internal postgres database regardless
    // of whether the uninstalls from the dataset's install targets succeed.
    cacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    val timer = Metrics.Uninstall.duration
      .labels(internalDBRecord.typeName, internalDBRecord.typeVersion)
      .startTimer()

    // Grab a plugin handler instance for this dataset type.
    val handler = PluginHandlers[internalDBRecord.typeName, internalDBRecord.typeVersion]

    if (handler == null) {
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

      if (projectID !in AppDatabaseRegistry) {
        log.warn("dataset {}/{} cannot be uninstalled from target project {} as the project is disabled", userID, datasetID, projectID)
        return@forEach
      }

      if (datasetShouldBeUninstalled(userID, datasetID, projectID)) {
        try {
          tryUninstallDataset(userID, datasetID, projectID, handler.client, internalDBRecord)
        } catch (e: Throwable) {
          log.error("dataset ${userID}/${projectID} uninstall from project $projectID failed: ", e)
        }
      }
    }

    timer.observeDuration()
  }

  private suspend fun tryUninstallDataset(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    handler: vdi.component.plugin.client.PluginHandlerClient,
    record: DatasetRecord
  ) {
    appDB.withTransaction(projectID) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedNotUninstalled) }

    val response = handler.postUninstall(datasetID, projectID)

    Metrics.Uninstall.count.labels(record.typeName, record.typeVersion, response.responseCode.toString()).inc()

    when (response.type) {
      UninstallResponseType.Success
      -> handleSuccessResponse(userID, datasetID, projectID)

      UninstallResponseType.BadRequest
      -> handleBadRequestResponse(userID, datasetID, projectID, response as UninstallBadRequestResponse)

      UninstallResponseType.UnexpectedError
      -> handleUnexpectedErrorResponse(userID, datasetID, projectID, response as UninstallUnexpectedErrorResponse)
    }
  }

  private fun datasetShouldBeUninstalled(userID: UserID, datasetID: DatasetID, projectID: ProjectID): Boolean {
    val dataset = appDB.accessor(projectID)!!.selectDataset(datasetID)

    if (dataset == null) {
      log.warn("dataset {}/{} does not appear in target project {}, cannot run uninstall", userID, datasetID, projectID)
      return false
    }

    if (dataset.isDeleted == DeleteFlag.DeletedAndUninstalled) {
      log.info("dataset {}/{} has already been successfully uninstalled from target project {}", userID, datasetID, projectID)
      return false
    }

    return true
  }

  private fun handleSuccessResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset handler server reports dataset {}/{} was successfully uninstalled from project {}", userID, datasetID, projectID)
    appDB.withTransaction(projectID) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedAndUninstalled) }
  }

  private fun handleBadRequestResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID, res: UninstallBadRequestResponse) {
    log.error("dataset handler server reports 400 error for uninstall on dataset {}/{}, project {}", userID, datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun handleUnexpectedErrorResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID, res: UninstallUnexpectedErrorResponse) {
    log.error("dataset handler server reports 500 for uninstall on dataset {}/{}, project {}", userID, datasetID, projectID)
    throw IllegalStateException(res.message)
  }
}
