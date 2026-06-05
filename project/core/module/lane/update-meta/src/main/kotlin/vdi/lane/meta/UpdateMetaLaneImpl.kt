package vdi.lane.meta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.core.async.WorkerPool
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDBTransaction
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.upsertDatasetRecord
import vdi.core.db.app.withTransaction
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.initializeDataset
import vdi.core.db.cache.withTransaction
import vdi.core.kafka.EventSource
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.EmptySuccessResponse
import vdi.core.plugin.client.response.ServiceErrorResponse
import vdi.core.plugin.client.response.ValidationErrorResponse
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.plugin.registry.PluginRegistry
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.getLatestDatasetPropertiesTimestamp
import vdi.core.util.orElse
import vdi.logging.logger
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetVisibility
import vdi.core.plugin.client.model.DataPropertiesFile as PluginDataPropsFile

internal class UpdateMetaLaneImpl(private val config: UpdateMetaLaneConfig, abortCB: AbortCB)
  : UpdateMetaLane
  , AbstractVDIModule(abortCB, logger<UpdateMetaLane>())
{
  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.eventTopic, config.kafkaConsumerConfig)
    val kr = requireKafkaRouter(config.kafkaRouterConfig)
    val wp = WorkerPool.create<UpdateMetaLane>(config.jobQueueSize, config.workerCount) {
      Metrics.updateMetaQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventKey)
            .map { UpdateMetaContext(it, dm, kr, logger) }
            .onEach { it.logger.info("received install-meta job from source {}", it.source) }
            .forEach { wp.submit { it.updateMetaIfNotInProgress() } }
      }
    }

    wp.stop()
    kc.close()
    kr.close()
    confirmShutdown()
  }

  /**
   * Avoid simultaneous update executions on a single dataset.
   */
  private suspend fun UpdateMetaContext.updateMetaIfNotInProgress() {
    if (datasetsInProgress.add(datasetID)) {
      try {
        updateMetaIfNotRecursive()
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      logger.info("meta update already in progress; ignoring meta event")
    }
  }

  /**
   * Avoid infinite update loops from events fired by this lane.
   */
  private suspend fun UpdateMetaContext.updateMetaIfNotRecursive() {
    try {
      if (source == EventSource.UPDATE_META_LANE) {
        logger.warn("attempted to recurse update meta")
      } else {
        updateMeta()
        events.sendReconciliationTrigger(ownerID, datasetID, EventSource.UPDATE_META_LANE)
      }
    } catch (e: PluginException) {
      e.log(logger::error)
    } catch (e: UpdateMetaException) {
      e.log(logger::error)
    } catch (e: Throwable) {
      UpdateMetaException(ownerID, datasetID.asString, cause = e).log(logger::error)
    }
  }

  private suspend fun UpdateMetaContext.updateMeta() {
    logger.debug("looking up dataset directory")

    // lookup the dataset directory for the given userID and datasetID
    val dir = store.getDatasetDirectory(ownerID, datasetID)

    // If the dataset directory is not usable, bail out.
    //
    // Don't worry about logging here, the `isUsable` method performs logging
    // specific to the reason that the dataset directory is not usable.
    if (!dir.isUsable())
      return

    // Load the dataset metadata from S3
    val datasetMeta = dir.getMetaFile().load()!!

    val metaTimestamp = dir.getMetaFile().lastModified()!!
    logger.debug("meta json timestamp is {}", metaTimestamp)

    val mappingTimestamp = dir.getLatestDatasetPropertiesTimestamp()
      ?: OriginTimestamp
    logger.debug("dataset properties timestamp is {}", mappingTimestamp)

    val timer = Metrics.MetaUpdates.duration
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
      .startTimer()

    // Attempt to select the dataset details from the cache DB
    val cachedDataset = cacheDB.selectDataset(datasetID)

    // If no dataset record was found in the cache DB then this is (likely) the
    // first event for the dataset coming through the pipeline.
    if (cachedDataset == null) {
      logger.debug("dataset details were not found for dataset, creating them")
      // If they were not found, construct them
      cacheDB.withTransaction { it.initializeDataset(datasetID, datasetMeta) }
    }

    // Else if the dataset is marked as deleted already, then bail here.
    else if (cachedDataset.isDeleted) {
      logger.info("refusing to update dataset meta; dataset marked as deleted")
      return
    }

    cacheDB.withTransaction { db ->
      // 1. Update meta info
      db.updateDatasetMeta(datasetID, datasetMeta)

      // 2. Sync Publications
      db.deletePublications(datasetID)
      db.tryInsertPublications(datasetID, datasetMeta.publications)

      // 3. Update meta timestamp
      db.updateMetaSyncControl(datasetID, metaTimestamp)
    }

    val ph = PluginHandlers[datasetMeta.type] orElse {
      logger.error("dataset declares a type of {} which is unknown to vdi", datasetMeta.type)
      return
    }

    datasetMeta.installTargets.forEach {
      withPlugin(datasetMeta, ph, it, dir)
        .tryUpdateTargetMeta(maxOf(metaTimestamp, mappingTimestamp))
    }

    timer.observeDuration()
  }

  private suspend fun UpdateMetaContext.WithPlugin.tryUpdateTargetMeta(metaTimestamp: OffsetDateTime) {
    try {
      updateTargetMeta(metaTimestamp)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw UpdateMetaException(ownerID, datasetID.asString, cause = e)
    }
  }

  private suspend fun UpdateMetaContext.WithPlugin.updateTargetMeta(metaTimestamp: OffsetDateTime) {
    if (!shouldUpdateTargetMeta(metaTimestamp))
      return

    // Ensure at least some record exists for the dataset before attempting
    // to write anything else to the target database.
    appDB.withTransaction(target, plugin.type) {
      it.tryInsertDataset(DatasetRecord(
        datasetID,
        // If the dataset didn't already exist, set it to private first
        meta.copy(visibility = DatasetVisibility.Private),
        PluginRegistry.require(meta.type).category,
      ))
    }

    // Record the dataset properties install status IF an install-meta call
    // happened.  If the call didn't happen, this value will be null.
    val datasetPropertiesInstallStatus = with(appDB.accessor(target, plugin.type)!!) {
      // Stop here if the dataset does not already have a successful data
      // install recorded.  The install-meta script requires installed data to
      // function.
      if (selectDatasetInstallMessage(datasetID, InstallType.Data)?.status != InstallStatus.Complete)
        return@with null

      appDB.withTransaction(target, plugin.type) {
        logger.debug("upserting install-meta message into app db")
        it.upsertInstallMetaMessage(datasetID, InstallStatus.Running)
      }

      // Attempt to run the target plugin's install-meta script.
      try {
         runPluginInstallMeta(metaTimestamp)
      } catch (e: Throwable) {
        logger.error("plugin error", PluginRequestException.installMeta(plugin.name, target, ownerID, datasetID, cause = e))
        InstallStatus.FailedInstallation
      }
    }

    var newMeta = meta

    // If the install-meta script failed...
    if (datasetPropertiesInstallStatus != InstallStatus.Complete) {
      // AND the user is attempting to promote the dataset to community, then
      // refuse to change the visibility.
      if (shouldRevertToPrivateOnDatasetPropertiesError(meta)) {
        logger.warn("refusing to make dataset public due to unsuccessful install-meta")
        newMeta = meta.copy(visibility = DatasetVisibility.Private)
      }
    }

    // Update/insert full set of dataset records.
    try {
      appDB.withTransaction(target, plugin.type) {
        val record = DatasetRecord(datasetID, newMeta, PluginRegistry.require(meta.type).category)
        it.upsertDatasetRecord(record, newMeta, metaTimestamp)

        // If install-meta didn't run, then no status has been set yet.  Set one
        // here indicating that we successfully wrote to the control tables at
        // least.
        if (datasetPropertiesInstallStatus == null)
          it.upsertInstallMetaMessage(datasetID, InstallStatus.Complete)
      }
    } catch (e: Throwable) {
      appDB.withTransaction(target, plugin.type) {
        it.upsertInstallMetaMessage(
          datasetID,
          InstallStatus.FailedInstallation,
          "installation failed due to unexpected database error"
        )
      }

      throw e
    }
  }

  private suspend fun UpdateMetaContext.WithPlugin.runPluginInstallMeta(metaTimestamp: OffsetDateTime): InstallStatus? {
    val dataPropFiles = directory.getDataPropertiesFiles()
      .map { PluginDataPropsFile(it.baseName, it.open()!!) }
      .asIterable()

    try {
      plugin.client.postInstallMeta(eventID, datasetID, target, meta, dataPropFiles).use { result ->
        Metrics.MetaUpdates.count.labels(
          meta.type.name.toString(),
          meta.type.version,
          result.status.toString(),
        ).inc()

        try {
          return when (result) {
            is EmptySuccessResponse -> {
              handleSuccessResponse()
              InstallStatus.Complete
            }
            is ValidationErrorResponse -> {
              handleValidationError(result)
              InstallStatus.FailedValidation
            }
            is ServiceErrorResponse -> {
              handleUnexpectedErrorResponse(result)
              InstallStatus.FailedInstallation
            }
          }
        } catch (e: Throwable) {
          logger.info("install-meta request to handler server failed with exception:", e)
          appDB.withTransaction(target, plugin.type) {
            try {
              it.upsertInstallMetaMessage(datasetID, InstallStatus.FailedInstallation)
            } catch (e: SQLException) {
              if (e.errorCode == 1) {
                logger.info("unique key constraint violation on install meta; assuming race condition")
              } else {
                throw e
              }
            }
          }
          throw e
        } finally {
          appDB.withTransaction(target, plugin.type) { it.upsertSyncControlMetaTimestamp(datasetID, metaTimestamp) }
        }
      }
    } finally {
      dataPropFiles.forEach {
        try { it.close() }
        catch (e: Throwable) { logger.error("failed to close s3 object {}", it.name, e)}
      }
    }
  }

  private fun UpdateMetaContext.WithPlugin.shouldUpdateTargetMeta(metaTimestamp: OffsetDateTime): Boolean {
    if (!plugin.appliesToProject(target)) {
      logger.warn("install target does not support data type; skipping meta update")
      return false
    }

    val appDb = appDB.accessor(target, plugin.type) orElse {
      logger.info("skipping meta update; target is disabled")
      return false
    }

    val failed = appDb
      .selectDatasetInstallMessages(datasetID)
      .any { it.status == InstallStatus.FailedInstallation || it.status == InstallStatus.FailedValidation }

    if (failed) {
      logger.info("skipping update due to previous failures")
      return false
    }

    // FIXME: Temporarily disabling meta timestamp check.  Meta updates will
    //        always happen until re-enabled.
//    val sync = appDb.selectDatasetSyncControlRecord(datasetID)
//      ?: return true
//
//    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
//      logger.info("skipping update; nothing has changed")
//      return false
//    }

    return true
  }

  private fun UpdateMetaContext.WithPlugin.handleSuccessResponse() {
    logger.info("dataset meta installed successfully")

    appDB.withTransaction(target, plugin.type) {
      it.upsertInstallMetaMessage(
        datasetID,
        InstallStatus.Complete
      )
    }
  }

  private fun UpdateMetaContext.WithPlugin.handleValidationError(response: ValidationErrorResponse) {
    logger.info("dataset meta validation failed")

    appDB.withTransaction(target, plugin.type) {
      it.upsertInstallMetaMessage(
        datasetID,
        InstallStatus.FailedValidation,
        response.getWarningsSequence().joinToString("\n")
      )
    }
  }

  private fun UpdateMetaContext.WithPlugin.handleUnexpectedErrorResponse(res: ServiceErrorResponse) {
    logger.error("status ${res.status}: ${res.message}")

    throw PluginException.installMeta(plugin.name, target, ownerID, datasetID, res.message)
  }

  private fun AppDBTransaction.upsertInstallMetaMessage(
    datasetID: DatasetID,
    status: InstallStatus,
    message: String? = null,
  ) {
    val message = DatasetInstallMessage(datasetID, InstallType.Meta, status, message)
    upsertDatasetInstallMessage(message)
  }

  context(ctx: UpdateMetaContext)
  private fun DatasetDirectory.isUsable(): Boolean {
    if (!exists()) {
      ctx.logger.warn("ignoring update event; dataset with has no directory")
      return false
    }

    if (hasDeleteFlag()) {
      logger.info("ignoring update event; dataset has delete flag")
      return false
    }

    if (!hasMetaFile()) {
      logger.warn("ignoring update event; dataset has which has no $DatasetMetaFilename file")
      return false
    }

    return true
  }

  /**
   * Determines if the metadata JSON visibility field should be reset to private
   * on failure of a dataset properties install attempt.
   *
   * This is done to avoid making promoting a dataset to community if required
   * data is not installed.
   */
  private fun UpdateMetaContext.WithPlugin.shouldRevertToPrivateOnDatasetPropertiesError(meta: DatasetMetadata): Boolean {
    val publicInTarget = appDB.accessor(target, meta.type)!!
      .selectDataset(datasetID)
      ?.isPublic ?: false

    // if the dataset is private in the target AND the meta says it should be
    // made public
    return !publicInTarget && meta.visibility != DatasetVisibility.Private
  }
}
