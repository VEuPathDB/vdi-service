package vdi.module.handler.delete.soft

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallBadRequestResponse
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallResponseType
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallUnexpectedErrorResponse
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase

internal class SoftDeleteTriggerHandlerImpl(private val config: SoftDeleteTriggerHandlerConfig)
  : SoftDeleteTriggerHandler
  , VDIServiceModuleBase("soft-delete-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.softDeleteTriggerTopic, config.kafkaConsumerConfig)
    val wp = WorkerPool("soft-delete-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.softDeleteQueueSize.inc(it.toDouble())
    }

    runBlocking {
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

    confirmShutdown()
  }

  private fun runJob(userID: UserID, datasetID: DatasetID) {
    val internalDBRecord = CacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("received uninstall event for a dataset that does not exist in the internal database")

    // Mark the dataset is deleted in the internal postgres database regardless
    // of whether the uninstalls from the dataset's install targets succeed.
    CacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    val timer = Metrics.uninstallationTimes
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

  private fun tryUninstallDataset(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    handler: PluginHandlerClient,
    record: DatasetRecord
  ) {
    AppDB.withTransaction(projectID) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedNotUninstalled) }

    val response = handler.postUninstall(datasetID, projectID)

    Metrics.uninstallations.labels(record.typeName, record.typeVersion, response.responseCode.toString()).inc()

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
    val dataset = AppDB.accessor(projectID)!!.selectDataset(datasetID)

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
    AppDB.withTransaction(projectID) { it.updateDatasetDeletedFlag(datasetID, DeleteFlag.DeletedAndUninstalled) }
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
