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
import vdi.core.async.WorkerPool
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.initializeDataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.ScriptErrorResponse
import vdi.core.plugin.client.response.ServerErrorResponse
import vdi.core.plugin.client.response.StreamSuccessResponse
import vdi.core.plugin.client.response.ValidationErrorResponse
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.getInstallReadyTimestamp
import vdi.io.plugin.FileName
import vdi.json.JSON
import vdi.logging.logger
import vdi.model.DatasetManifestFilename
import vdi.model.DatasetMetaFilename
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetManifest
import vdi.core.metrics.Metrics.Import as Metrics
import vdi.io.plugin.responses.ValidationResponse as ValidationBody

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
      Metrics.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .map { ImportContext(it, dm, logger) }
            .onEach { it.logger.info("received import job from source ${it.source}") }
            .forEach { wp.submit { it.tryHandleImportEvent() } }
      }
    }

    kLogger.info("shutting down worker pool")
    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun ImportContext.tryHandleImportEvent() {
    try {
      handleImportEvent()
    } catch (e: PluginException) {
      e.log(logger::error)
    } catch (e: Throwable) {
      PluginException.import("N/A", ownerID, datasetID, cause = e).log(logger::error)
    }
  }

  private suspend fun ImportContext.handleImportEvent() {
    // lookup the dataset in S3
    val datasetDir = store.getDatasetDirectory(ownerID, datasetID)

    // If the dataset directory doesn't have all the necessary components, then
    // bail here.
    if (!datasetDir.isUsable()) {
      logger.debug("dataset dir is not usable")
      return
    }

    // Since we've decided we're in a state where we can attempt to process the
    // event for this dataset, ensure we aren't already processing an import for
    // this dataset.  If we are, bail here, if we aren't, then mark the dataset
    // as in progress (add it to our set of active imports) and proceed.
    lock.withLock {
      if (datasetID in activeIDs) {
        logger.info("skipping import event; dataset is already being processed")
        return
      }

      activeIDs.add(datasetID)
    }

    try {
      processImportJob(datasetDir)
    } finally {
      lock.withLock {
        activeIDs.remove(datasetID)
      }
    }
  }

  private suspend fun ImportContext.processImportJob(datasetDir: DatasetDirectory) {
    // Load the dataset metadata from S3
    val datasetMeta = datasetDir.getMetaFile().load()!!

    val timer = Metrics.duration
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
      .startTimer()

    // lookup the target dataset in the cache database to ensure it
    // exists, initializing the dataset if it doesn't yet exist.
    with(cacheDB.selectDataset(datasetID)) {
      if (this == null) {
        logger.info("initializing dataset in cache db")
        cacheDB.withTransaction { it.initializeDataset(datasetID, datasetMeta) }
      } else {
        if (isDeleted) {
          logger.info("skipping import event; dataset is marked as deleted in the cache db")
          return
        }
      }
    }

    val impStatus = cacheDB.selectImportControl(datasetID)!!

    if (impStatus != DatasetImportStatus.Queued) {
      logger.info("skipping import event; dataset is already in status {}", impStatus)
      return
    }

    if (!datasetDir.hasImportReadyFile()) {
      logger.info("skipping import event; import-ready file is not yet in object store")
      return
    }

    try {
      val handler = PluginHandlers[datasetMeta.type] ?:
        return logger.warn(
          "attempted to import dataset, but no plugin is currently enabled for data type {}:{}",
          datasetMeta.type.name,
          datasetMeta.type.version,
        )

      withPlugin(datasetMeta, handler, datasetDir).processImportJob()
    } catch (e: Throwable) {
      cacheDB.withTransaction { tran ->
        tran.updateImportControl(datasetID, DatasetImportStatus.Failed)
        tran.tryInsertImportMessages(datasetID, listOf("process error: ${e.message}"))
      }

      throw e as? PluginException ?: PluginException.import("N/A", ownerID, datasetID, cause = e)
    } finally {
      timer.observeDuration()
    }
  }

  private suspend fun ImportContext.WithPlugin.processImportJob() {
    try {
      cacheDB.withTransaction {
        logger.info("attempting to insert import control record (if one does not exist)")
        it.upsertImportControl(datasetID, DatasetImportStatus.InProgress)
      }

      try {
        datasetDir.getImportReadyFile()
          .open()!!
          .use { plugin.client.postImport(eventID, datasetID, meta, it) }
          .use { result ->
            Metrics.count.labels(meta.type.name.toString(), meta.type.version, result.status.code.toString()).inc()

            when (result) {
              is StreamSuccessResponse   -> handleImportSuccessResult(result)
              is ValidationErrorResponse -> handleImportInvalidResult(result)
              is ScriptErrorResponse     -> handleScriptError(result)
              is ServerErrorResponse     -> handleServerError(result)
            }
          }
      } catch (e: S34KError) { // don't mix up minio errors with request errors
        throw PluginException.import(plugin.name, ownerID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.import(plugin.name, ownerID, datasetID, cause = e)
      }

    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.import(plugin.name, ownerID, datasetID, cause = e)
    }
  }

  private fun ImportContext.WithPlugin.handleImportSuccessResult(result: StreamSuccessResponse) {
    logger.info("dataset imported successfully")

    var warnings: ValidationBody? = null
    var hasData  = false
    var manifest: DatasetManifest? = null

    result.asZip().forEach { (entry, stream) ->
      when (entry.name) {
        DatasetManifestFilename -> {
          logger.debug("writing manifest contents to object store")

          manifest = JSON.readValue(stream)
          datasetDir.putManifestFile(manifest!!)
        }

        FileName.WarningsFile -> {
          logger.debug("deserializing warnings")
          warnings = JSON.readValue(stream)
        }

        FileName.DataFile -> {
          logger.debug("writing install-ready zip contents to object store")
          datasetDir.getInstallReadyFile().put { stream }
          hasData = true
        }

        else -> {
          logger.error("unrecognized zip entry received from plugin server: {}", entry.name)
          stream.skip(Long.MAX_VALUE)
        }
      }
    }

    if (!hasData || manifest == null)
      throw IllegalStateException("missing either data zip or manifest file for dataset $ownerID/$datasetID")

    // Record the status update to the cache DB
    cacheDB.withTransaction { transaction ->
      if (
        warnings?.basicWarnings?.isNotEmpty() == true
        || warnings?.communityWarnings?.isNotEmpty() == true
      ) {
        transaction.tryInsertImportMessages(
          datasetID,
          warnings.basicWarnings + warnings.communityWarnings,
        )
      }

      transaction.tryInsertInstallFiles(datasetID, manifest.installReadyFiles)
      transaction.updateDataSyncControl(datasetID, datasetDir.getInstallReadyTimestamp() ?: OffsetDateTime.now())
      transaction.updateImportControl(datasetID, DatasetImportStatus.Complete)
    }
  }

  private fun ImportContext.WithPlugin.handleImportInvalidResult(result: ValidationErrorResponse) {
    logger.info("validation failed with {} warnings", result.getWarningsSequence().count())

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Invalid)
      it.tryInsertImportMessages(
        datasetID,
        Iterable { result.getWarningsSequence().iterator() },
      )
    }
  }

  private fun ImportContext.WithPlugin.handleScriptError(result: ScriptErrorResponse) {
    logger.error("import failed due to script error: {}", result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.tryInsertImportMessages(datasetID, listOf(result.message))
    }

    throw PluginException.import(plugin.name, ownerID, datasetID, result.message)
  }

  private fun ImportContext.WithPlugin.handleServerError(result: ServerErrorResponse) {
    logger.error("import failed due to plugin server error: {}", result.message)

    cacheDB.withTransaction {
      it.updateImportControl(datasetID, DatasetImportStatus.Failed)
      it.tryInsertImportMessages(datasetID, listOf(result.message))
    }

    throw PluginException.import(plugin.name, ownerID, datasetID, result.message)
  }

  context(ctx: ImportContext)
  private fun DatasetDirectory.isUsable(): Boolean {
    if (!exists()) {
      ctx.logger.warn("dataset no longer has a directory in the data store")
      return false
    }

    if (hasDeleteFlag()) {
      ctx.logger.info("dataset has a delete flag, ignoring it")
      return false
    }

    if (!hasMetaFile()) {
      ctx.logger.info("dataset does not yet have a {} file, ignoring it", DatasetMetaFilename)
      return false
    }

    if (!hasImportReadyFile()) {
      ctx.logger.info("dataset does not yet have an import ready file, ignoring it")
      return false
    }

    return true
  }
}
