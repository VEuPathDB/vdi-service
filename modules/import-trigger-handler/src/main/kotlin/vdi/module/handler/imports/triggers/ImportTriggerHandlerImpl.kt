package vdi.module.handler.imports.triggers

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import java.lang.IllegalStateException
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.components.common.ShutdownSignal
import vdi.components.common.fields.DatasetID
import vdi.components.common.jobs.WorkerPool
import vdi.components.datasets.DatasetManager
import vdi.components.json.JSON
import vdi.components.kafka.KafkaConsumer
import vdi.components.kafka.KafkaProducer
import vdi.components.kafka.router.KafkaRouter
import vdi.components.kafka.router.KafkaRouterFactory
import vdi.components.kafka.triggers.ImportTrigger
import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig

class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig) : ImportTriggerHandler {

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
    TODO("this should use a ShutdownSignal to indicate that the process wants to stop, then block and wait for shutdown confirmation")
  }

  private suspend fun run() {
    // get a handle on the cache database
    val ds = CacheDB(config.cacheDBConfig)

    val s3 = try {
      S3Api.newClient(config.s3Config)
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create S3 client instance", e)
      throw e
    }

    val bucket = s3.buckets[config.s3Bucket]

    if (bucket == null) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("s3 bucket {} does not appear to exist", config.s3Bucket)
      throw IllegalStateException("s3 bucket ${config.s3Bucket} does not appear to exist")
    }

    val dm = DatasetManager(bucket)

    // get a Kafka consumer for reading messages from the import trigger topic
    val kc = try {
      KafkaConsumer(config.kafkaConfig.importTriggerTopic, config.kafkaConfig.consumerConfig)
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create KafkaConsumer instance", e)
      throw e
    }

    // get a Kafka producer for writing messages to the import result topic
    val kr = try {
      KafkaRouterFactory(config.kafkaConfig.routerConfig).newKafkaRouter()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create KafkaProducer instance", e)
    }

    // set up executor pool of some configurable number of executors
    // TODO: the queue size could be different than the worker count
    val wp = WorkerPool(config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      // Spin up the worker pool (in the background)
      wp.start(this)

      // While the shutdown trigger has not yet been triggered
      while (!shutdownTrigger.isTriggered()) {

        // Read messages from the kafka consumer
        kc.receive().asSequence()
          // Filter the messages down to only those we care about
          .filter { it.key == config.kafkaConfig.importTriggerMessageKey }
          // Pop the value out of the message
          .map { it.value }
          // Translate value to an import trigger message
          .map {
            try {
              JSON.readValue<ImportTrigger>(it)
            } catch (e: Throwable) {
              log.warn("received an invalid message body from Kafka: {}", it)
              null
            }
          }
          // Filter out the null values (invalid dataset IDs)
          .filterNotNull()
          .forEach { trigger ->
            // lookup the target dataset in the cache database
            val dataset = try {
              ds.selectDataset(trigger.datasetID)
            } catch (e: Throwable) {
              log.error("failed to lookup dataset ${trigger.datasetID}", e)
              return@forEach
            }

            // If the target dataset was not found, skip this message
            if (dataset == null) {
              log.warn("received a dataset ID for a non-existent dataset from Kafka: {}", trigger.datasetID)
              return@forEach
            }

            // If the target dataset is no longer in the awaiting import status,
            // skip this message as it is a duplicate.
            if (dataset.importStatus != DatasetImportStatus.AwaitingImport) {
              log.debug("received a duplicate import trigger for dataset {}", trigger.datasetID)
              return@forEach
            }

            // There is a race condition here between checking the status and
            // updating it, some other worker could claim it and mark it as in
            // progress and start processing it.  We don't care.  The end result
            // will be the same either way, the check above is just to filter
            // out as much duplicated work as we reasonably can.

            // TODO: figure out where the worker's work should start

            // update the cache-db record for the target dataset to say it is
            // importing
            ds.updateDatasetStatus(trigger.datasetID, DatasetImportStatus.Importing)

            // retrieve the dataset data from S3
            val uploadFiles = dm.getDatasetDirectory(trigger.userID, trigger.datasetID)
              .getUploadFiles()

            if (uploadFiles.isEmpty()) {
              log.error("dataset {} had zero files in its upload files directory", trigger.datasetID)
              return@forEach
            } else if (uploadFiles.size > 1) {
              log.error("dataset {} had more than one file in its upload files directory", trigger.datasetID)
              return@forEach
            }

            // TODO: Dataset upload file should have a name property!!

            val uploadFile = uploadFiles[0]

            // Create a temp file that will be deleted after this call (maybe using a `use` block or something)
            // Download the upload tar file into that temp file
            //

            // TODO: create an API wrapping client for the handler server

            // call the plugin handler import endpoint with the dataset data
            // update the cache-db record for the target dataset with its import result status
            // write any import warnings or errors to the cache db
            // write the result (on success only) of the import to S3
            // post a message to the kafka producer with the result status
          }

      }

      log.info("shutting down worker pool")

      wp.stop()
      shutdownConfirm.trigger()
    }
  }
}