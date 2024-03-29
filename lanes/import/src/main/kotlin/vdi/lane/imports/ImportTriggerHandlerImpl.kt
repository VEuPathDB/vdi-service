package vdi.lane.imports

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.isNull
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.json.JSON
import vdi.component.async.WorkerPool
import vdi.component.db.cache.CacheDBTransaction
import vdi.component.db.cache.model.DatasetImpl
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetMetaImpl
import vdi.component.db.cache.withTransaction
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractVDIModule
import vdi.component.plugin.client.response.imp.*
import vdi.component.plugin.mapping.PluginHandlers
import vdi.component.s3.DatasetManager
import vdi.lane.imports.config.ImportTriggerHandlerConfig
import vdi.lane.imports.model.WarningsFile
import java.nio.file.Path
import java.time.OffsetDateTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.*

internal class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig)
  : ImportTriggerHandler
  , AbstractVDIModule("import-trigger-handler")
{
  private val log = logger()

  private val lock = ReentrantLock()

  private val activeIDs = HashSet<DatasetID>(24)

  private val cacheDB = vdi.component.db.cache.CacheDB()

  override val name = "import lane"

  override suspend fun run() {
    log.trace("run()")

    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.kafkaConfig.importTriggerTopic, config.kafkaConfig.consumerConfig)
    val wp = WorkerPool("import-trigger-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.Import.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        // While the shutdown trigger has not yet been triggered
        while (!isShutDown()) {
          // Read messages from the kafka consumer
          kc.fetchMessages(config.kafkaConfig.importTriggerMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received import job for dataset $userID/$datasetID from source $source")
              wp.submit { importJob(dm, userID, datasetID) }
            }
        }

        log.info("shutting down worker pool")
        wp.stop()
      }

      // Spin up the worker pool (in the background)
      wp.start()
    }

    kc.close()
    confirmShutdown()
  }

  private suspend fun importJob(dm: DatasetManager, userID: UserID, datasetID: DatasetID) {
    log.trace("importJob(dm=..., userID=$userID, datasetID=$datasetID")

    // lookup the dataset in S3
    val datasetDir = dm.getDatasetDirectory(userID, datasetID)

    // If the dataset directory doesn't have all the necessary components, then
    // bail here.
    if (!datasetDir.isUsable(datasetID, userID)) {
      log.debug("dataset dir for dataset $datasetID (user $userID) is not usable")
      return
    }

    // Since we've decided we're in a state where we can attempt to process the
    // event for this dataset, ensure we aren't already processing an import for
    // this dataset.  If we are, bail here, if we aren't, then mark the dataset
    // as in progress (add it to our set of active imports) and proceed.
    lock.withLock {
      if (datasetID in activeIDs) {
        log.info("skipping import event for dataset $userID/$datasetID as it is already being processed")
        return
      }

      activeIDs.add(datasetID)
    }

    try {
      processImportJob(userID, datasetID, datasetDir)
    } finally {
      lock.withLock {
        activeIDs.remove(datasetID)
      }
    }
  }

  private suspend fun processImportJob(userID: UserID, datasetID: DatasetID, datasetDir: vdi.component.s3.DatasetDirectory) {
    // Load the dataset metadata from S3
    val datasetMeta = datasetDir.getMetaFile().load()!!

    val timer = Metrics.Import.duration
      .labels(datasetMeta.type.name, datasetMeta.type.version)
      .startTimer()

    // lookup the target dataset in the cache database to ensure it
    // exists, initializing the dataset if it doesn't yet exist.
    with(cacheDB.selectDataset(datasetID)) {
      if (isNull()) {
        log.info("initializing dataset $userID/$datasetID")
        cacheDB.initializeDataset(datasetID, datasetMeta)
      } else {
        if (isDeleted) {
          log.info("skipping import event for dataset $userID/$datasetID as it is marked as deleted in the cache db")
          return
        }
        log.info("Dataset already initialized. Handling import event.")
      }
    }

    val impStatus = cacheDB.selectImportControl(datasetID)!!

    if (impStatus != DatasetImportStatus.Queued) {
      log.info("skipping import event for dataset $userID/$datasetID as it is already in status $impStatus")
      return
    }

    if (!datasetDir.hasImportReadyFile()) {
      log.info("received an import event for dataset $userID/$datasetID where the import-ready file doesn't exist yet")
      return
    }

    try {
      val handler = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version] or {
        log.error("No plugin handler registered for dataset type ${datasetMeta.type.name}")
        throw IllegalStateException("No plugin handler registered for dataset type ${datasetMeta.type.name}")
      }

      cacheDB.withTransaction {
        log.info("attempting to insert import control record (if one does not exist)")
        it.upsertImportControl(datasetID, DatasetImportStatus.InProgress)
      }

      val result = datasetDir.getImportReadyFile()
        .loadContents()!!
        .use { handler.client.postImport(datasetID, datasetMeta, it) }

      Metrics.Import.count.labels(datasetMeta.type.name, datasetMeta.type.version, result.responseCode.toString()).inc()

      when (result.type) {
        ImportResponseType.Success         -> handleImportSuccessResult(
          datasetID,
          result as ImportSuccessResponse,
          datasetDir
        )
        ImportResponseType.BadRequest      -> handleImportBadRequestResult(datasetID, result as ImportBadRequestResponse)
        ImportResponseType.ValidationError -> handleImportInvalidResult(datasetID, result as ImportValidationErrorResponse)
        ImportResponseType.UnhandledError  -> handleImport500Result(userID, datasetID, result as ImportUnhandledErrorResponse)
      }
    } catch (e: Throwable) {
      log.debug("import request to handler server failed with exception:", e)
      cacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.Failed)
        tran.tryInsertImportMessages(datasetID, "Process error: ${e.message}")
      }
      throw e
    } finally {
      timer.observeDuration()
    }
  }

  private fun handleImportSuccessResult(datasetID: DatasetID, result: ImportSuccessResponse, dd: vdi.component.s3.DatasetDirectory) {
    log.info("dataset handler server reports dataset $datasetID imported successfully")

    // Create a temp directory to use as a workspace for the following process
    TempFiles.withTempDirectory { tempDirectory ->

      TempFiles.withTempPath { tempArchive ->
        result.resultArchive
          .buffered()
          .use { input -> tempArchive.outputStream().buffered().use { output -> input.transferTo(output) } }

        Zip.zipEntries(tempArchive).forEach { (entry, stream) ->
          tempDirectory.resolve(entry.name)
            .createFile()
            .outputStream()
            .buffered()
            .use { stream.transferTo(it) }
        }
      }

      // Consume the warnings file and delete it from the data directory.
      val warnings = tempDirectory.resolve("warnings.json")
        .consumeAsJSON<WarningsFile>()
        .warnings

      // Remove the meta file and delete it from the data directory.
      tempDirectory.resolve(DatasetMetaFilename).deleteExisting()

      // Consume the manifest file and delete it from the data directory.
      val manifest = tempDirectory.resolve(DatasetManifestFilename)
        .consumeAsJSON<VDIDatasetManifest>()

      // After deleting the warnings.json file, the metadata JSON file, and the
      // manifest JSON file the remaining files should be the ones we care about
      // for importing into the data files directory in S3
      val dataFiles = tempDirectory.listDirectoryEntries()

      // For each data file, push to S3

      TempFiles.withTempFile { tempFile ->
        Zip.compress(tempFile, dataFiles)
        dd.putInstallReadyFile { tempFile.inputStream() }
      }

      dd.putManifestFile(manifest)

      // Record the status update to the cache DB
      cacheDB.withTransaction { transaction ->
        if (warnings.isNotEmpty()) {
          transaction.upsertImportMessages(datasetID, warnings.joinToString("\n"))
        }

        transaction.tryInsertInstallFiles(datasetID, manifest.dataFiles)
        transaction.updateDataSyncControl(datasetID, dd.getInstallReadyTimestamp() ?: OffsetDateTime.now())
        transaction.updateImportControl(datasetID, DatasetImportStatus.Complete)
      }
    }
  }

  private fun handleImportBadRequestResult(datasetID: DatasetID, result: ImportBadRequestResponse) {
    log.error("dataset handler server reports 400 error for dataset $datasetID, message: ${result.message}")
    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, result.message)
    }
    throw IllegalStateException(result.message)
  }

  private fun handleImportInvalidResult(datasetID: DatasetID, result: ImportValidationErrorResponse) {
    log.info("dataset handler server reports dataset $datasetID failed validation")
    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Invalid)
      it.upsertImportMessages(datasetID, result.warnings.joinToString("\n"))
    }
  }

  private fun handleImport500Result(userID: UserID, datasetID: DatasetID, result: ImportUnhandledErrorResponse) {
    log.error("dataset handler server reports 500 for dataset $userID/$datasetID, message ${result.message}")
    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, result.message)
    }
    throw IllegalStateException(result.message)
  }

  private fun vdi.component.s3.DatasetDirectory.isUsable(datasetID: DatasetID, userID: UserID): Boolean {
    if (!exists()) {
      log.warn("got an import event for dataset $userID/$datasetID which no longer has a directory")
      return false
    }

    if (hasDeleteFlag()) {
      log.info("got an import event for dataset $userID/$datasetID which has a delete flag, ignoring it")
      return false
    }

    if (!hasMetaFile()) {
      log.info("got an import event for dataset $userID/$datasetID which does not yet have a $DatasetMetaFilename file, ignoring it")
      return false
    }

    if (!hasImportReadyFile()) {
      log.info("got an import event for dataset $userID/$datasetID which does not yet have a processed upload, ignoring it")
      return false
    }

    return true
  }

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    log.trace("CacheDBTransaction.initSyncControl(datasetID: $datasetID)")
    tryInsertSyncControl(VDISyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))
  }

  private fun vdi.component.db.cache.CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
    log.trace("CacheDB.initializeDataset(datasetID: $datasetID, meta: $meta)")
    openTransaction().use {
      try {
        // Insert a new dataset record
        it.tryInsertDataset(DatasetImpl(
          datasetID    = datasetID,
          typeName     = meta.type.name,
          typeVersion  = meta.type.version,
          ownerID      = meta.owner,
          isDeleted    = false,
          created      = meta.created,
          origin       = meta.origin,
          importStatus = DatasetImportStatus.Queued,
          inserted     = OffsetDateTime.now(),
        ))

        // insert metadata for the dataset
        it.tryInsertDatasetMeta(DatasetMetaImpl(
          datasetID   = datasetID,
          visibility  = meta.visibility,
          name        = meta.name,
          summary     = meta.summary,
          description = meta.description,
          sourceURL   = meta.sourceURL,
        ))

        // Insert an import control record for the dataset
        it.tryInsertImportControl(datasetID, DatasetImportStatus.Queued)

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
