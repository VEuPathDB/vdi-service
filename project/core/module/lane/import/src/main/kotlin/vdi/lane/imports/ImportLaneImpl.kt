package vdi.lane.imports

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.kotlin.loggerOf
import org.veupathdb.lib.s3.s34k.errors.S34KError
import java.time.OffsetDateTime
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.json.JSON
import vdi.core.async.WorkerPool
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.CacheDBTransaction
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.SyncControlRecord
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.imp.*
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.api.internal.FileName
import vdi.model.api.internal.WarningResponse
import vdi.model.data.DatasetID
import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata
import vdi.model.data.UserID
import vdi.util.zip.zipEntries

internal class ImportLaneImpl(private val config: ImportLaneConfig, abortCB: AbortCB)
  : ImportLane
  , AbstractVDIModule(abortCB, logger<ImportLane>())
{
  private val kLogger = loggerOf(ImportLane::class.java).delegate

  private val lock = Mutex()

  private val activeIDs = HashSet<DatasetID>(24)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaConfig)
    val wp = WorkerPool.create<ImportLane>(config.jobQueueSize, config.workerCount) {
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
      if (this == null) {
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
      val handler = PluginHandlers[datasetMeta.type] ?:
        return kLogger.warn("attempted to import dataset {}/{} but no plugin is currently enabled for dataset type {}", userID, datasetID, datasetMeta.type)

      processImportJob(handler, datasetMeta, userID, datasetID, datasetDir)
    } catch (e: Throwable) {
      cacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.Failed)
        tran.tryInsertImportMessages(datasetID, listOf("process error: ${e.message}"))
      }

      throw e as? PluginException ?: PluginException.import("N/A", userID, datasetID, cause = e)
    } finally {
      timer.observeDuration()
    }
  }

  private suspend fun processImportJob(
    handler: PluginHandler,
    meta: DatasetMetadata,
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
        throw PluginException.import(handler.name, userID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.import(handler.name, userID, datasetID, cause = e)
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
      throw PluginException.import(handler.name, userID, datasetID, cause = e)
    }
  }

  private fun handleImportSuccessResult(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    result: ImportSuccessResponse,
    dd: DatasetDirectory
  ) {
    kLogger.info("dataset handler server reports dataset {}/{} imported successfully in plugin {}", userID, datasetID, handler.name)

    var warnings = emptyList<String>()
    var hasData = false
    var manifest: DatasetManifest? = null

    result.resultArchive.zipEntries()
      .forEach { (entry, stream) ->
        when (entry.name) {
          DatasetManifestFilename -> {
            kLogger.debug("writing manifest contents to object store for dataset {}/{}", userID, datasetID)

            manifest = JSON.readValue(stream)
            dd.putManifestFile(manifest!!)
          }

          FileName.WarningsFile -> {
            kLogger.debug("deserializing warnings for dataset {}/{}", userID, datasetID)
            warnings = JSON.readValue<WarningResponse>(stream).warnings.toList()
          }

          FileName.DataFile -> {
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
        transaction.upsertImportMessages(datasetID, warnings)
      }

      transaction.tryInsertInstallFiles(datasetID, manifest!!.installReadyFiles)
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
    kLogger.error("plugin reports 400 error for dataset {}/{} in plugin {}: {}", userID, datasetID, handler.name, result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, listOf(result.message))
    }

    throw PluginException.import(handler.name, userID, datasetID, result.message)
  }

  private fun handleImportInvalidResult(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    result: ImportValidationErrorResponse
  ) {
    kLogger.info("plugin reports dataset {}/{} failed validation in plugin {}", userID, datasetID, handler.name)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Invalid)
      it.upsertImportMessages(datasetID, result.warnings)
    }
  }

  private fun handleImport500Result(handler: PluginHandler, userID: UserID, datasetID: DatasetID, result: ImportUnhandledErrorResponse) {
    kLogger.error("plugin reports 500 for dataset {}/{} in plugin {}: {}", userID, datasetID, handler.name, result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.upsertImportMessages(datasetID, listOf(result.message))
    }

    throw PluginException.import(handler.name, userID, datasetID, result.message)
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

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: DatasetMetadata) {
    openTransaction().use {
      try {
        // Insert a new dataset record
        it.tryInsertDataset(DatasetImpl(
          datasetID    = datasetID,
          type         = meta.type,
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
        it.tryInsertDatasetProjects(datasetID, meta.installTargets)

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
