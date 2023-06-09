package vdi.module.handler.imports.triggers

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.isNull
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.handler.client.response.imp.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.model.triggers.ImportTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.nio.file.Path
import java.time.OffsetDateTime
import kotlin.IllegalStateException
import kotlin.io.path.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase
import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig
import vdi.module.handler.imports.triggers.model.WarningsFile

internal class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig)
  : ImportTriggerHandler
  , VDIServiceModuleBase("import-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    log.trace("run()")

    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val kc = requireKafkaConsumer(config.kafkaConfig.importTriggerTopic, config.kafkaConfig.consumerConfig)
    val wp = WorkerPool("import-trigger-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt())

    runBlocking(Dispatchers.IO) {
      launch {
        // While the shutdown trigger has not yet been triggered
        while (!isShutDown()) {
          // Read messages from the kafka consumer
          kc.fetchMessages(config.kafkaConfig.importTriggerMessageKey, ImportTrigger::class)
            .forEach { (userID, datasetID) ->
              log.info("received import job for dataset {}, user {}", datasetID, userID)
              wp.submit { importJob(dm, userID, datasetID) }
            }
        }

        log.info("shutting down worker pool")
        wp.stop()
      }

      // Spin up the worker pool (in the background)
      wp.start()
    }

     confirmShutdown()
  }

  private fun importJob(dm: DatasetManager, userID: UserID, datasetID: DatasetID) {
    log.trace("importJob(dm=..., userID={}, datasetID={}", userID, datasetID)

    // lookup the dataset in S3
    val datasetDir = dm.getDatasetDirectory(userID, datasetID)

    if (!datasetDir.isUsable(datasetID, userID)) {
      log.debug("dataset dir for dataset {} (user {}) is not usable", datasetID, userID)
      return
    }

    // Load the dataset metadata from S3
    val datasetMeta = datasetDir.getMeta().load()!!

    val timer = Metrics.importTimes
      .labels(datasetMeta.type.name, datasetMeta.type.version)
      .startTimer()

    // lookup the target dataset in the cache database to ensure it
    // exists, initializing the dataset if it doesn't yet exist.
    with(CacheDB.selectDataset(datasetID)) {
      if (isNull()) {
        log.info("initializing dataset {} for user {}", datasetID, userID)
        CacheDB.initializeDataset(datasetID, datasetMeta)
      } else {
        if (isDeleted)
          log.info("skipping import event for dataset {}/{} as it is marked as deleted in the cache db", userID, datasetID)
          return
      }
    }

    CacheDB.withTransaction {
      log.info("attempting to insert import control record (if one does not exist)")
      it.tryInsertImportControl(datasetID, DatasetImportStatus.Importing)
    }

    try {
      log.debug("fetching upload files for dataset {} (user {})", datasetID, userID)
      val uploadFiles = datasetDir.getUploadFiles()

      if (uploadFiles.isEmpty()) {
        log.info("received an import event where the upload file doesn't yet exist. Dataset: {}, User: {}", datasetID, userID)
        return
      }

      if (uploadFiles.size > 1) {
        log.warn("received an import event for a dataset with more than one upload file.  using file {} for dataset {}, user {}", uploadFiles[0].name, datasetID, userID)
      }

      val handler = PluginHandlers.get(datasetMeta.type.name, datasetMeta.type.version) or {
        log.error("No plugin handler registered for dataset type {}", datasetMeta.type.name)
        throw IllegalStateException("No plugin handler registered for dataset type ${datasetMeta.type.name}")
      }

      val result = uploadFiles[0]
        .open()!!
        .use { handler.client.postImport(datasetID, datasetMeta, it) }

      Metrics.imports.labels(datasetMeta.type.name, datasetMeta.type.version, result.responseCode.toString()).inc()

      when (result.type) {
        ImportResponseType.Success         -> handleImportSuccessResult(
          datasetID,
          result as ImportSuccessResponse,
          datasetDir
        )
        ImportResponseType.BadRequest      -> handleImportBadRequestResult(datasetID, result as ImportBadRequestResponse)
        ImportResponseType.ValidationError -> handleImportInvalidResult(datasetID, result as ImportValidationErrorResponse)
        ImportResponseType.UnhandledError  -> handleImport500Result(datasetID, result as ImportUnhandledErrorResponse)
      }
    } catch (e: Throwable) {
      log.debug("import request to handler server failed with exception:", e)
      CacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.ImportFailed)
        tran.tryInsertImportMessages(datasetID, "Process error: ${e.message}")
      }
      throw e
    } finally {
      timer.observeDuration()
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

        transaction.updateDataSyncControl(datasetID, dd.getLatestDataTimestamp(OffsetDateTime.now()))
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

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    log.trace("CacheDBTransaction.initSyncControl(datasetID: {})", datasetID)
    tryInsertSyncControl(VDISyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))
  }

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
    log.trace("CacheDB.initializeDataset(datasetID: {}, meta: {})", datasetID, meta)
    openTransaction().use {
      try {
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
      } catch (e: Throwable) {
        it.rollback()
        throw e
      }
    }
  }

  private inline fun <reified T> Path.consumeAsJSON(): T {
    val value = inputStream().use { JSON.readValue<T>(it) }
    deleteExisting()
    return value
  }
}
