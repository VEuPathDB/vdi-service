package vdi.lane.imports

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.lib.s3.s34k.errors.S34KError
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.util.isNull
import org.veupathdb.vdi.lib.json.JSON
import java.time.OffsetDateTime
import vdi.lib.async.WorkerPool
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.CacheDBTransaction
import vdi.lib.db.cache.model.DatasetImpl
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.withTransaction
import vdi.lib.db.model.SyncControlRecord
import vdi.lib.logging.logger
import vdi.lib.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.imp.*
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers
import vdi.lib.s3.DatasetDirectory
import vdi.lib.s3.DatasetObjectStore

internal class ImportTriggerHandlerImpl(private val config: ImportTriggerHandlerConfig, abortCB: AbortCB)
  : ImportTriggerHandler
  , AbstractVDIModule("import", abortCB, logger<ImportTriggerHandler>())
{
  private val kLogger = logger().delegate

  private val lock = Mutex()

  private val activeIDs = HashSet<DatasetID>(24)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaConfig)
    val wp = WorkerPool("import", config.jobQueueSize, config.workerCount) {
      Metrics.Import.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .forEach { (userID, datasetID, source) ->
              kLogger.info("received import job for dataset $userID/$datasetID from source $source")
              wp.submit { tryHandleImportEvent(dm, userID, datasetID) }
            }
      }
    }

    kLogger.info("shutting down worker pool")
    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun tryHandleImportEvent(dm: DatasetObjectStore, userID: UserID, datasetID: DatasetID) {
    try {
      handleImportEvent(dm, userID, datasetID)
    } catch (e: PluginException) {
      e.log(kLogger::error)
    } catch (e: Throwable) {
      PluginException.import("N/A", userID, datasetID, cause = e).log(kLogger::error)
    }
  }

  private suspend fun handleImportEvent(dm: DatasetObjectStore, userID: UserID, datasetID: DatasetID) {
    // lookup the dataset in S3
    val datasetDir = dm.getDatasetDirectory(userID, datasetID)

    // If the dataset directory doesn't have all the necessary components, then
    // bail here.
    if (!datasetDir.isUsable(datasetID, userID)) {
      kLogger.debug("dataset dir for dataset {}/{} is not usable", userID, datasetID)
      return
    }

    // Since we've decided we're in a state where we can attempt to process the
    // event for this dataset, ensure we aren't already processing an import for
    // this dataset.  If we are, bail here, if we aren't, then mark the dataset
    // as in progress (add it to our set of active imports) and proceed.
    lock.withLock {
      if (datasetID in activeIDs) {
        kLogger.info("skipping import event for dataset {}/{} as it is already being processed", userID, datasetID)
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

  private suspend fun processImportJob(userID: UserID, datasetID: DatasetID, datasetDir: DatasetDirectory) {
    // Load the dataset metadata from S3
    val datasetMeta = datasetDir.getMetaFile().load()!!

    val timer = Metrics.Import.duration
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
      .startTimer()

    // lookup the target dataset in the cache database to ensure it
    // exists, initializing the dataset if it doesn't yet exist.
    with(cacheDB.selectDataset(datasetID)) {
      if (isNull()) {
        kLogger.info("initializing dataset {}/{}", userID, datasetID)
        cacheDB.initializeDataset(datasetID, datasetMeta)
      } else {
        if (isDeleted) {
          kLogger.info("skipping import event for dataset {}/{} as it is marked as deleted in the cache db", userID, datasetID)
          return
        }
        kLogger.info("Dataset already initialized. Handling import event.")
      }
    }

    val impStatus = cacheDB.selectImportControl(datasetID)!!

    if (impStatus != DatasetImportStatus.Queued) {
      kLogger.info("skipping import event for dataset {}/{} as it is already in status {}", userID, datasetID, impStatus)
      return
    }

    if (!datasetDir.hasImportReadyFile()) {
      kLogger.info("skipping import event for dataset {}/{} as the import-ready file doesn't exist yet", userID, datasetID)
      return
    }

    try {
      val handler = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version] ?:
        return kLogger.warn("attempted to import dataset {}/{} but no plugin is currently enabled for dataset type {}", userID, datasetID, datasetMeta.type)

      processImportJob(handler, datasetMeta, userID, datasetID, datasetDir)
    } catch (e: Throwable) {
      cacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.Failed)
        tran.tryInsertImportMessages(datasetID, "Process error: ${e.message}")
      }

      throw e as? PluginException ?: PluginException.import("N/A", userID, datasetID, cause = e)
    } finally {
      timer.observeDuration()
    }
  }

  private suspend fun processImportJob(
    handler: PluginHandler,
    meta: VDIDatasetMeta,
    userID: UserID,
    datasetID: DatasetID,
    datasetDir: DatasetDirectory
  ) {
    try {
      cacheDB.withTransaction {
        kLogger.info("attempting to insert import control record (if one does not exist) for dataset {}/{}", userID, datasetID)
        it.upsertImportControl(datasetID, DatasetImportStatus.InProgress)
      }

      val result = try {
        datasetDir.getImportReadyFile()
          .loadContents()!!
          .use { handler.client.postImport(datasetID, meta, it) }
      } catch (e: S34KError) { // don't mix up minio errors with request errors
        throw PluginException.import(handler.displayName, userID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.import(handler.displayName, userID, datasetID, cause = e)
      }

      Metrics.Import.count.labels(meta.type.name.toString(), meta.type.version, result.responseCode.toString()).inc()

      when (result.type) {
        ImportResponseType.Success -> handleImportSuccessResult(
          handler,
          userID,
          datasetID,
          result as ImportSuccessResponse,
          datasetDir
        )

        ImportResponseType.BadRequest -> handleImportBadRequestResult(
          handler,
          userID,
          datasetID,
          result as ImportBadRequestResponse
        )

        ImportResponseType.ValidationError -> handleImportInvalidResult(
          handler,
          userID,
          datasetID,
          result as ImportValidationErrorResponse
        )

        ImportResponseType.UnhandledError -> handleImport500Result(
          handler,
          userID,
          datasetID,
          result as ImportUnhandledErrorResponse,
        )
      }
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.import(handler.displayName, userID, datasetID, cause = e)
    }
  }

  private fun handleImportSuccessResult(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    result: ImportSuccessResponse,
    dd: DatasetDirectory
  ) {
    kLogger.info("dataset handler server reports dataset {}/{} imported successfully in plugin {}", userID, datasetID, handler.displayName)

    var warnings = emptyList<String>()
    var hasData = false
    var manifest: VDIDatasetManifest? = null

    Zip.zipEntries(result.resultArchive)
      .forEach { (entry, stream) ->
        when (entry.name) {
          DatasetManifestFilename -> {
            kLogger.debug("writing manifest contents to object store for dataset {}/{}", userID, datasetID)

            manifest = JSON.readValue(stream)
            dd.putManifestFile(manifest!!)
          }

          WarningsFileName -> {
            kLogger.debug("deserializing warnings for dataset {}/{}", userID, datasetID)
            warnings = JSON.readValue<WarningsFile>(stream).warnings
          }

          DataZipName -> {
            kLogger.debug("writing install-ready zip contents to object store for dataset {}/{}", userID, datasetID)
            dd.getInstallReadyFile().writeContents(stream)
            hasData = true
          }

          else -> {
            kLogger.error("unrecognized zip entry received from plugin server for dataset {}/{}: {}", userID, datasetID, entry.name)
            stream.skip(Long.MAX_VALUE)
          }
        }
      }

    if (!hasData || manifest == null) {
      throw IllegalStateException("missing either data zip or manifest file for dataset $userID/$datasetID")
    }

    // Record the status update to the cache DB
    cacheDB.withTransaction { transaction ->
      if (warnings.isNotEmpty()) {
        transaction.upsertImportMessages(datasetID, warnings.joinToString("\n"))
      }

      transaction.tryInsertInstallFiles(datasetID, manifest!!.dataFiles)
      transaction.updateDataSyncControl(datasetID, dd.getInstallReadyTimestamp() ?: OffsetDateTime.now())
      transaction.updateImportControl(datasetID, DatasetImportStatus.Complete)
    }
  }

  private fun handleImportBadRequestResult(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    result: ImportBadRequestResponse
  ) {
    kLogger.error("plugin reports 400 error for dataset {}/{} in plugin {}: {}", userID, datasetID, handler.displayName, result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, result.message)
    }

    throw PluginException.import(handler.displayName, userID, datasetID, result.message)
  }

  private fun handleImportInvalidResult(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    result: ImportValidationErrorResponse
  ) {
    kLogger.info("plugin reports dataset {}/{} failed validation in plugin {}", userID, datasetID, handler.displayName)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Invalid)
      it.upsertImportMessages(datasetID, result.warnings.joinToString("\n"))
    }
  }

  private fun handleImport500Result(handler: PluginHandler, userID: UserID, datasetID: DatasetID, result: ImportUnhandledErrorResponse) {
    kLogger.error("plugin reports 500 for dataset {}/{} in plugin {}: {}", userID, datasetID, handler.displayName, result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, result.message)
    }

    throw PluginException.import(handler.displayName, userID, datasetID, result.message)
  }

  private fun DatasetDirectory.isUsable(datasetID: DatasetID, userID: UserID): Boolean {
    if (!exists()) {
      kLogger.warn("got an import event for dataset {}/{} which no longer has a directory", userID, datasetID)
      return false
    }

    if (hasDeleteFlag()) {
      kLogger.info("got an import event for dataset {}/{} which has a delete flag, ignoring it", userID, datasetID)
      return false
    }

    if (!hasMetaFile()) {
      kLogger.info("got an import event for dataset {}/{} which does not yet have a {} file, ignoring it", userID, datasetID, DatasetMetaFilename)
      return false
    }

    if (!hasImportReadyFile()) {
      kLogger.info("got an import event for dataset {}/{} which does not yet have a processed upload, ignoring it", userID, datasetID)
      return false
    }

    return true
  }

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    tryInsertSyncControl(SyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))
  }

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
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
        it.tryInsertDatasetMeta(datasetID, meta)

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
}
