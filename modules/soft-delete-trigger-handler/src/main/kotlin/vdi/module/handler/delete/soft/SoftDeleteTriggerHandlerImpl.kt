package vdi.module.handler.delete.soft

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
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
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.SoftDeleteTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.module.handler.delete.soft.config.SoftDeleteTriggerHandlerConfig

internal class SoftDeleteTriggerHandlerImpl(private val config: SoftDeleteTriggerHandlerConfig) : SoftDeleteTriggerHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  override suspend fun start() {
    if (!started) {
      log.info("starting soft-delete-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering soft-delete-trigger-handler module shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val kc = requireKafkaConsumer()
    val dm = DatasetManager(requireS3Bucket(requireS3Client()))
    val wp = WorkerPool("soft-delete-workers", config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      launch(Dispatchers.IO) {
        while (!shutdownTrigger.isTriggered()) {
          kc.selectTriggers()
            .forEach { (userID, datasetID) -> runJob(userID, datasetID, dm) }
        }

        wp.stop()
      }

      wp.start()
    }
  }

  private fun runJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    val dir = dm.getDatasetDirectory(userID, datasetID)

    if (!dir.exists())
      throw IllegalStateException("got an uninstall event for non-existent dataset $datasetID (user $userID)")

    val meta = dir.getMeta().load()
      ?: throw IllegalStateException("got an uninstall event for a dataset that has no meta file: dataset $datasetID, user $userID")

    CacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }

    val handler = PluginHandlers[meta.type.name]
      ?: throw IllegalStateException("got an uninstall event for a dataset type that has no associated handler.  dataset $datasetID, user $userID")

    meta.projects.forEach { projectID ->
      if (!handler.appliesToProject(projectID)) {
        log.warn("type handler for type {} does not apply to project {} (dataset {}, user {})", handler.type, projectID, datasetID, userID)
        return@forEach
      }

      runJob(userID, datasetID, projectID, handler.client)
    }
  }

  private fun runJob(userID: UserID, datasetID: DatasetID, projectID: ProjectID, handler: PluginHandlerClient) {
    val response = handler.postUninstall(datasetID, projectID)

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

  private suspend inline fun <T> safeExec(err: String, fn: () -> T): T =
    try {
      fn()
    } catch (e: Throwable) {
      log.error(err, e)
      stop()
      throw e
    }

  private suspend fun requireKafkaConsumer() = safeExec("failed to create KafkaConsumer instance") {
    KafkaConsumer(config.softDeleteTriggerTopic, config.kafkaConsumerConfig)
  }

  private suspend fun requireS3Client() = safeExec("failed to create S3 client instance") {
    S3Api.newClient(config.s3Config)
  }

  private suspend fun requireS3Bucket(s3: S3Client) = safeExec("failed to lookup target S3 bucket") {
    s3.buckets[config.s3Bucket] ?: throw IllegalStateException("bucket ${config.s3Bucket} does not exist!")
  }

  private fun KafkaConsumer.selectTriggers() =
    receive()
      .asSequence()
      .filter {
        if (it.key == config.softDeleteTriggerMessageKey) {
          true
        } else {
          log.warn("filtering out message with key {} as ti does not match expected key {}", it.key, config.softDeleteTriggerMessageKey)
          false
        }
      }
      .map {
        try {
          JSON.readValue<SoftDeleteTrigger>(it.value)
        } catch (e: Throwable) {
          log.warn("received an invalid message body from Kafka: {}", it)
          null
        }
      }
      .filterNotNull()

}

