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
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.handler.client.response.imp.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.ImportTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.nio.file.Path
import java.time.OffsetDateTime
import kotlin.IllegalStateException
import kotlin.io.path.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig
import vdi.module.handler.imports.triggers.model.WarningsFile

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
    log.trace("run()")

    val dm = DatasetManager(S3Api.requireClient().requireBucket(config.s3Bucket))
    val kc = requireKafkaConsumer()
    val wp = WorkerPool("import-trigger-workers", config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      // Spin up the worker pool (in the background)
      wp.start(this)

      // While the shutdown trigger has not yet been triggered
      while (!shutdownTrigger.isTriggered()) {

        // Read messages from the kafka consumer
        kc.selectImportTriggers()
          .forEach { (userID, datasetID) ->
            log.info("received import job for dataset {}, user {}", datasetID, userID)
            wp.submit { importJob(dm, userID, datasetID) }
          }

      }

      log.info("shutting down worker pool")

      wp.stop()
      shutdownConfirm.trigger()
    }
  }

  private fun importJob(dm: DatasetManager, userID: UserID, datasetID: DatasetID) {
    log.trace("importJob(dm=..., userID={}, datasetID={}", userID, datasetID)
    // lookup the dataset in S3
    val datasetDir = dm.getDatasetDirectory(userID, datasetID)

    if (!datasetDir.isUsable(datasetID, userID))
      return

    // Load the dataset metadata from S3
    val datasetMeta = datasetDir.getMeta().load()!!

    // lookup the target dataset in the cache database to ensure it
    // exists, initializing the dataset if it doesn't yet exist.
    CacheDB.selectDataset(datasetID) or {
      log.info("initializing dataset {} for user {}", datasetID, userID)
      CacheDB.initializeDataset(datasetID, datasetMeta)
    }

    CacheDB.withTransaction {
      it.tryInsertImportControl(datasetID, DatasetImportStatus.Importing)
    }

    try {
      val uploadFiles = datasetDir.getUploadFiles()

      if (uploadFiles.isEmpty()) {
        log.info("received an import event where the upload file doesn't yet exist. Dataset: {}, User: {}", datasetID, userID)
        return
      }

      if (uploadFiles.size > 1) {
        log.warn("received an import event for a dataset with more than one upload file.  using file {} for dataset {}, user {}", uploadFiles[0].name, datasetID, userID)
      }

      val handler = PluginHandlers[datasetMeta.type.name] or {
        log.error("No plugin handler registered for dataset type {}", datasetMeta.type.name)
        throw IllegalStateException("No plugin handler registered for dataset type ${datasetMeta.type.name}")
      }

      val result = uploadFiles[0]
        .open()!!
        .use { handler.client.postImport(datasetID, datasetMeta, it) }

      when (result) {
        is ImportSuccessResponse         -> handleImportSuccessResult(datasetID, result, datasetDir)
        is ImportBadRequestResponse      -> handleImportBadRequestResult(datasetID, result)
        is ImportValidationErrorResponse -> handleImportInvalidResult(datasetID, result)
        is ImportUnhandledErrorResponse  -> handleImport500Result(datasetID, result)
      }
    } catch (e: Throwable) {
      CacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.ImportFailed)
        tran.tryInsertImportMessages(datasetID, "Process error: ${e.message}")
      }
      throw e
    }
  }

  private fun handleImportSuccessResult(datasetID: DatasetID, result: ImportSuccessResponse, dd: DatasetDirectory) {
    log.info("dataset handler server reports dataset {} imported successfully", datasetID)

    // Create a temp directory to use as a workspace for the following process
    TempFiles.withTempDirectory { tempDirectory ->

      // Write the tar file result to a temp file that we can immediately unpack
      // into our temp directory
      TempFiles.withTempPath { tempArchive ->
        result.resultArchive.use { input -> tempArchive.outputStream().use { output -> input.transferTo(output) } }
        Tar.decompressWithGZip(tempArchive, tempDirectory)
      }

      // Consume the warnings file and delete it from the data directory.
      val warnings = tempDirectory.resolve("warnings.json")
        .consumeAsJSON<WarningsFile>()
        .warnings

      // Remove the meta file and delete it from the data directory.
      tempDirectory.resolve("meta.json").deleteExisting()

      // Consume the manifest file and delete it from the data directory.
      val manifest = tempDirectory.resolve("manifest.json")
        .consumeAsJSON<VDIDatasetManifest>()

      // After deleting warnings.json, meta.json, and manifest.json the
      // remaining files should be the ones we care about for importing into the
      // data files directory in S3
      val dataFiles = tempDirectory.listDirectoryEntries()

      // For each data file, push to S3
      dataFiles.forEach { dataFile -> dd.putDataFile(dataFile.name, dataFile::inputStream) }
      dd.putManifest(manifest)

      // Record the status update to the cache DB
      CacheDB.withTransaction { transaction ->
        if (warnings.isNotEmpty()) {
          transaction.upsertImportMessages(datasetID, warnings.joinToString("\n"))
        }

        transaction.updateDataSyncControl(datasetID, OffsetDateTime.now())
        transaction.updateImportControl(datasetID, DatasetImportStatus.Imported)
      }
    }
  }

  private fun handleImportBadRequestResult(datasetID: DatasetID, result: ImportBadRequestResponse) {
    log.error("dataset handler server reports 400 error for dataset {}, message: {}", datasetID, result.message)
    throw IllegalStateException(result.message)
  }

  private fun handleImportInvalidResult(datasetID: DatasetID, result: ImportValidationErrorResponse) {
    log.info("dataset handler server reports dataset {} failed validation", datasetID)
    CacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.ImportFailed)
      it.upsertImportMessages(datasetID, result.warnings.joinToString("\n"))
    }
  }

  private fun handleImport500Result(datasetID: DatasetID, result: ImportUnhandledErrorResponse) {
    log.error("dataset handler server reports 500 for dataset {}, message {}", datasetID, result.message)
    throw IllegalStateException(result.message)
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
      .onEach { log.debug("received message from kafka with key: \"${it.key}\"") }
      .filter {
        if (it.key == config.kafkaConfig.importTriggerMessageKey) {
          true
        } else {
          log.warn("received message from kafka with incorrect message key.  expected {}, got {}", config.kafkaConfig.importTriggerMessageKey, it.key)
          false
        }
      }
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

  private inline fun <reified T> Path.consumeAsJSON(): T {
    val value = inputStream().use { JSON.readValue<T>(it) }
    deleteExisting()
    return value
  }
}
