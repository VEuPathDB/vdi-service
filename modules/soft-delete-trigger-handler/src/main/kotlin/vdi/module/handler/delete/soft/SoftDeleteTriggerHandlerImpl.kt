package vdi.module.handler.delete.soft

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallBadRequestResponse
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallResponseType
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallUnexpectedErrorResponse
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.kafka.model.triggers.SoftDeleteTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase

internal class SoftDeleteTriggerHandlerImpl(private val config: SoftDeleteTriggerHandlerConfig)
  : SoftDeleteTriggerHandler
  , VDIServiceModuleBase("soft-delete-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.softDeleteTriggerTopic, config.kafkaConsumerConfig)
    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val wp = WorkerPool("soft-delete-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.softDeleteTriggerMessageKey, SoftDeleteTrigger::class)
            .forEach { (userID, datasetID) -> runJob(userID, datasetID, dm) }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }

  private fun runJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    // Fetch the dataset directory from S3
    val dir = dm.getDatasetDirectory(userID, datasetID)

    // If the directory doesn't exist, then something has gone terribly wrong.
    if (!dir.exists())
      throw IllegalStateException("got an uninstall event for non-existent dataset $datasetID (user $userID)")

    // Load the dataset metadata from S3
    val meta = dir.getMeta().load()
      ?: throw IllegalStateException("got an uninstall event for a dataset that has no meta file: dataset $datasetID, user $userID")

    val timer = Metrics.uninstallationTimes
      .labels(meta.type.name, meta.type.version)
      .startTimer()

    // Mark the dataset as deleted in the cache DB
    CacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    // Grab a handler instance for this dataset type.
    val handler = PluginHandlers[meta.type.name]
      ?: throw IllegalStateException("got an uninstall event for a dataset type that has no associated handler.  dataset $datasetID, user $userID")

    // Iterate through the projects that the metadata is targeting and call
    // the uninstall path on each.
    meta.projects.forEach { projectID ->
      if (!handler.appliesToProject(projectID)) {
        log.warn("type handler for type {} does not apply to project {} (dataset {}, user {})", handler.type, projectID, datasetID, userID)
        return@forEach
      }

      runJob(userID, datasetID, projectID, handler.client, meta)
    }

    timer.observeDuration()
  }

  private fun runJob(
    userID:    UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    handler:   PluginHandlerClient,
    meta:      VDIDatasetMeta,
  ) {
    val response = handler.postUninstall(datasetID, projectID)

    Metrics.uninstallations.labels(meta.type.name, meta.type.version, response.responseCode.toString()).inc()

    when (response.type) {
      UninstallResponseType.Success
      -> handleSuccessResponse(userID, datasetID, projectID)

      UninstallResponseType.BadRequest
      -> handleBadRequestResponse(datasetID, projectID, response as UninstallBadRequestResponse)

      UninstallResponseType.UnexpectedError
      -> handleUnexpectedErrorResponse(datasetID, projectID, response as UninstallUnexpectedErrorResponse)
    }
  }

  private fun handleSuccessResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset handler server reports dataset {} was successfully uninstalled from project {}", datasetID, projectID)

    val appDB = try {
      AppDB.transaction(projectID)
    } catch (e: Throwable) {
      throw IllegalStateException("dataset $datasetID (user $userID) is linked to project $projectID which is currently unknown to the vdi service", e)
    }

    try {
      appDB.updateDatasetDeletedFlag(datasetID, true)
      appDB.commit()
    } catch (e: Throwable) {
      log.error("failed to update dataset deleted flag for dataset $datasetID (user $userID)", e)
      appDB.rollback()
      return
    } finally {
      appDB.close()
    }
  }

  private fun handleBadRequestResponse(datasetID: DatasetID, projectID: ProjectID, res: UninstallBadRequestResponse) {
    log.error("dataset handler server reports 400 error for uninstall on dataset {}, project {}", datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun handleUnexpectedErrorResponse(datasetID: DatasetID, projectID: ProjectID, res: UninstallUnexpectedErrorResponse) {
    log.error("dataset handler server reports 500 for uninstall on dataset {}, project {}", datasetID, projectID)
    throw IllegalStateException(res.message)
  }
}
