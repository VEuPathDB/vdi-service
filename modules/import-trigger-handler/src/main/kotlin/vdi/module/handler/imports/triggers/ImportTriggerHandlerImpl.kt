package vdi.module.handler.imports.triggers

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClientConfig
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.ImportTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.InstallTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.lang.IllegalStateException
import java.time.OffsetDateTime
import kotlinx.coroutines.runBlocking
import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig

internal class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig) : ImportTriggerHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  override suspend fun start() {
    if (!started) {
      log.info("starting import-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering import-trigger-handler shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }


  private suspend fun run() {
    val s3     = S3Api.requireClient()
    val bucket = s3.requireBucket(config.s3Bucket)
    val dm     = DatasetManager(bucket)
    val kc     = requireKafkaConsumer()
    val kr     = requireKafkaRouter()
    val wp     = WorkerPool(config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      // Spin up the worker pool (in the background)
      wp.start(this)

      // While the shutdown trigger has not yet been triggered
      while (!shutdownTrigger.isTriggered()) {

        // Read messages from the kafka consumer
        kc.selectImportTriggers()
          .forEach { (userID, datasetID) ->
            wp.submit {
              // lookup the dataset in S3
              val dir = dm.getDatasetDirectory(userID, datasetID)

              if (!dir.isUsable(datasetID, userID))
                return@submit

              // Load the dataset metadata from S3
              val datasetMeta = dir.getMeta().load()!!

              // lookup the target dataset in the cache database to ensure it
              // exists, initializing the dataset if it doesn't yet exist.
              CacheDB.selectDataset(datasetID)
                ?: CacheDB.initializeDataset(datasetID, datasetMeta)


              val uploadFiles = dir.getUploadFiles()

              if (uploadFiles.isEmpty()) {
                log.info("received an import event where the upload file doesn't yet exist. Dataset: {}, User: {}", datasetID, userID)
                return@submit
              }

              if (uploadFiles.size > 1) {
                log.warn("received an import event for a dataset with more than one upload file.  using file {} for dataset {}, user {}", uploadFiles[0].name, datasetID, userID)
              }

              for (projectID in datasetMeta.projects) {
                val handler = PluginHandlers[projectID]
                  .also {
                    if (it == null)
                      log.error("dataset {} targets project {} which is not registered with the plugin handlers", datasetID, projectID)
                  }
                  ?: continue

                val result = uploadFiles[0]
                  .open()!!
                  .use { handler.client.postImport(datasetID, datasetMeta, it) }
              }

              // TODO:
              //   If the result was "success":
              //     . receive and unpack the result tar file
              //     . read and record the warnings from the included warnings json file
              //     . record the job as successful
              //     . Upload the data files unpacked from the tar file to s3
              //     . send a success result message to the appropriate kafka topic
              //   If the result was "bad request":
              //     . Emit an error log message about it
              //     . Record the job as failed for internal reasons
              //     . send a failure result message to the appropriate kafka topic
              //   If the result was "validation error":
              //     . Emit an info log message about it
              //     . record the job as failed for validation reasons
              //     . send a failure result message to the appropriate kafka topic
              //   If the result was "unhandled exception":
              //     . Emit a warning log message about it
              //     . record the job as failed for internal reasons
              //     . send a failure result message to the appropriate kafka topic

            }
          }

      }

      log.info("shutting down worker pool")

      wp.stop()
      shutdownConfirm.trigger()
    }
  }

  private fun DatasetDirectory.isUsable(datasetID: DatasetID, userID: UserID): Boolean {
    if (!exists()) {
      log.warn("got an import event for a dataset directory that does not exist?  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    if (hasDeleteFlag()) {
      log.info("got an import event for a dataset with a delete flag, ignoring it.  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    if (!hasMeta()) {
      log.info("got an import event for a dataset that does not yet have a meta.json file, ignoring it.  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    return true
  }

  private suspend fun requireKafkaConsumer() = safeExec("failed to create KafkaConsumer instance") {
    KafkaConsumer(config.kafkaConfig.importTriggerTopic, config.kafkaConfig.consumerConfig)
  }

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaConfig.routerConfig).newKafkaRouter()
  }

  private suspend inline fun <T> safeExec(err: String, fn: () -> T): T =
    try {
      fn()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error(err, e)
      throw e
    }

  private suspend fun S3Api.requireClient() = safeExec("failed to create S3 client instance") { newClient(config.s3Config) }

  private suspend fun S3Client.requireBucket(name: BucketName): S3Bucket {
    val bucket = buckets[name]

    if (bucket == null) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("s3 bucket {} does not appear to exist", config.s3Bucket)
      throw IllegalStateException("s3 bucket ${config.s3Bucket} does not appear to exist")
    }

    return bucket
  }

  private fun KafkaConsumer.selectImportTriggers() =
    receive()
      .asSequence()
      .filter { it.key == config.kafkaConfig.importTriggerMessageKey }
      .map {
        try {
          JSON.readValue<ImportTrigger>(it.value)
        } catch (e: Throwable) {
          log.warn("received an invalid message body from Kafka: {}", it)
          null
        }
      }
      .filterNotNull()

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    tryInsertSyncControl(VDISyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))
  }

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
    openTransaction().use {

      // Insert a new dataset record
      it.tryInsertDataset(DatasetImpl(
        datasetID   = datasetID,
        typeName    = meta.type.name,
        typeVersion = meta.type.version,
        ownerID     = meta.owner,
        isDeleted   = false,
        created     = OffsetDateTime.now(),
        DatasetImportStatus.AwaitingImport
      ))

      // insert metadata for the dataset
      it.tryInsertDatasetMeta(DatasetMetaImpl(
        datasetID   = datasetID,
        name        = meta.name,
        summary     = meta.summary,
        description = meta.description,
      ))

      // Insert an import control record for the dataset
      it.tryInsertImportControl(datasetID, DatasetImportStatus.AwaitingImport)

      // insert project links for the dataset
      it.tryInsertDatasetProjects(datasetID, meta.projects)

      // insert a sync control record for the dataset using an old timestamp
      // that will predate any possible upload timestamp.
      it.initSyncControl(datasetID)
    }
  }
}