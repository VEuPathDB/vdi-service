package vdi.lane.install

import io.prometheus.client.Histogram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.lib.s3.s34k.errors.S34KError
import java.io.InputStream
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.core.async.WorkerPool
import vdi.core.db.app.AppDB
import vdi.core.db.app.isUniqueConstraintViolation
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.withTransaction
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.withTransaction
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.model.DataPropertiesFile
import vdi.core.plugin.client.response.*
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.getInstallReadyTimestamp
import vdi.core.util.orElse
import vdi.logging.logger
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.core.metrics.Metrics.Install as Metrics

internal class InstallDataLaneImpl(private val config: InstallDataLaneConfig, abortCB: AbortCB)
  : InstallDataLane
  , AbstractVDIModule(abortCB, logger<InstallDataLane>())
{
  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.consumerConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val wp = WorkerPool.create<InstallDataLane>(config.jobQueueSize, config.workerPoolSize) {
      Metrics.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey).forEach { msg ->
            InstallationContext(msg, dm, logger)
              .also { it.logger.info("received install job from source {}", msg.eventSource) }
              .also { wp.submit { it.tryInstallData() } }
          }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun InstallationContext.tryInstallData() {
    if (datasetsInProgress.add(datasetID)) {
      try {
        installData()
      } catch (e: PluginException) {
        e.log(logger::error)
      } catch (e: Throwable) {
        PluginException.installData("N/A", "N/A", ownerID, datasetID, cause = e).log(logger::error)
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      logger.info("data installation already in progress for dataset; ignoring install event")
    }
  }

  /**
   * Executes the installation on the target dataset on all target projects that
   * are relevant to the dataset's type.
   *
   * This method filters out datasets that do not exist yet, have no sync
   * control record, have no dataset record, or are not yet import completed.
   *
   * This method filters out installs for app dbs that are not relevant for the
   * target dataset type.
   *
   * This method filters out installs for datasets that have no type handler
   * configured.
   */
  private suspend fun InstallationContext.installData() {
    logger.debug("looking up dataset directory")

    val dir = store.getDatasetDirectory(ownerID, datasetID)

    // if the directory doesn't yet exist in S3, then how did we even get here?
    if (!dir.exists()) {
      logger.error("received an install trigger for a dataset whose minio directory does not exist")
      return
    }

    // Lookup the sync control record in the postgres DB
    val syncControl = cacheDB.selectSyncControl(datasetID)

    // If the sync control record was not found in postgres, then we are likely
    // seeing this event due to a cross campus S3 sync, and the metadata JSON
    // file hasn't landed yet.
    if (syncControl == null) {
      logger.info("skipping install data event; dataset does not yet have a sync control record in the cache DB")
      return
    }

    // Lookup the dataset record in the postgres DB
    val cdbDataset = cacheDB.selectDataset(datasetID)

    // If the dataset record doesn't yet exist in postgres DB then we caught
    // this event _while_ the metadata JSON event was being handled due to a
    // cross campus S3 sync, and we aren't yet ready for an install event.
    if (cdbDataset == null) {
      logger.info("skipping install data event; dataset does not yet have a record in the cache DB")
      return
    }

    // If the dataset has been marked as deleted, then we should bail here
    // because we don't process deleted datasets.
    if (cdbDataset.isDeleted) {
      logger.info("skipping install data event; dataset has been marked as deleted in cache DB")
      return
    }

    // If the dataset is not yet import complete then we caught this event due
    // to the import process actively happening, bail out here.
    if (!dir.hasInstallReadyFile()) {
      logger.info("skipping install data event; dataset is not yet import complete")
      return
    }

    // Load the metadata so that we can iterate through the target projects.
    val meta = dir.getMetaFile().load()!!

    // Get a handler instance for the target dataset type.
    val handler = PluginHandlers[meta.type]

    // If there is no handler for the target type, we shouldn't have gotten
    // here, but we can bail now to prevent issues.
    if (handler == null) {
      logger.error("skipping install data event; no handler configured for dataset type {}", meta.type)
      return
    }

    try {
      val installableFileTimestamp = dir.getInstallReadyTimestamp()
      if (installableFileTimestamp == null) {
        logger.error("could not fetch last modified timestamp for installable file")
        return
      }

      cacheDB.withTransaction { it.updateDataSyncControl(datasetID, installableFileTimestamp) }

      val success = dir.withInstallFiles { metaStream, manifestStream, uploadStream, dataPropFiles ->
        meta.installTargets
          .all { projectID ->
            // If the handler doesn't apply to the target project, again we
            // shouldn't be here, but we can bail out now with a warning.
            if (!handler.appliesToProject(projectID)) {
              logger.warn(
                "skipping install data event; handler for type {} does not apply to project {}",
                meta.type,
                projectID,
              )
              return@all true
            }

            withPlugin(meta, handler, projectID).installData(
              installableFileTimestamp,
              metaStream,
              manifestStream,
              uploadStream,
              dataPropFiles,
            )
          }
      }

      if (success && meta.revisionHistory != null)
        tryMarkRevised(meta)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(handler.name, "N/A", ownerID, datasetID, cause = e)
    }
  }

  /**
   * Executes the installation of the target dataset into a singular target
   * project that is confirmed relevant to the dataset's type.
   *
   * This method filters out datasets that have no record in the App DB yet,
   * that are marked as deleted, or that have a non-"ready-for-reinstall" status
   * in the target App DB.
   *
   * This method calls out to the handler server and deals with the response
   * status that it gets in reply.
   */
  private suspend fun InstallationContext.WithPlugin.installData(
    installFileTimestamp: OffsetDateTime,
    meta:                 InputStream,
    manifest:             InputStream,
    data:                 InputStream,
    dataPropFiles:        Iterable<DataPropertiesFile>,
  ): Boolean {
    var timer: Histogram.Timer? = null

    try {
      val appDB = appDB.accessor(target, type) orElse {
        logger.info("skipping install event; target project is disabled.")
        return false
      }

      val dataset = appDB.selectDataset(datasetID) orElse  {
        logger.info("skipping install event; no dataset record is present")
        return false
      }

      if (dataset.deletionState != DeleteFlag.NotDeleted) {
        logger.info("skipping install event; dataset marked as deleted")
        return false
      }

      val status = appDB.selectDatasetInstallMessage(datasetID, InstallType.Data)

      timer = Metrics.duration.labels(dataset.type.name.toString(), dataset.type.version).startTimer()

      if (status == null) {
        var race = false
        this@InstallDataLaneImpl.appDB.withTransaction(target, type) {
          try {
            it.insertDatasetInstallMessage(
              DatasetInstallMessage(
                datasetID,
                InstallType.Data,
                InstallStatus.Running,
                null
              )
            )
          } catch (e: SQLException) {
            if (it.isUniqueConstraintViolation(e)) {
              logger.info("unique constraint violation when writing install data message; assuming race condition and ignoring")
              race = true
            } else {
              throw e
            }
          }
        }

        if (race)
          return false
      } else {
        if (status.status != InstallStatus.ReadyForReinstall) {
          logger.info("skipping install event; dataset status is {}", status.status)
          return false
        }
      }

      return try {
        plugin.client.postInstallData(eventID, datasetID, target, meta, manifest, data, dataPropFiles).use { response ->
          Metrics.count.labels(dataset.type.name.toString(), dataset.type.version, response.status.code.toString()).inc()

          when (response) {
            is SuccessWithWarningsResponse -> {
              handleSuccessResponse(response, installFileTimestamp)
              true
            }

            is ValidationErrorResponse -> {
              handleValidationFailureResponse(response, installFileTimestamp)
              false
            }

            is MissingDependencyResponse -> {
              handleMissingDependenciesResponse(response, installFileTimestamp)
              false
            }

            is ScriptErrorResponse -> {
              handleScriptErrorResponse(response, installFileTimestamp)
              false
            }

            is ServerErrorResponse -> {
              handleServerErrorResponse(response, installFileTimestamp)
              false
            }
          }
        }
      } catch (e: S34KError) { // Don't mix up minio errors with request errors.
        throw PluginException.installData(plugin.name, target, ownerID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.installData(plugin.name, target, ownerID, datasetID, cause = e)
      }
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(plugin.name, target, ownerID, datasetID, cause = e)
    } finally {
      timer?.observeDuration()
    }
  }

  private fun InstallationContext.WithPlugin.handleSuccessResponse(
    res:              SuccessWithWarningsResponse,
    updatedTimestamp: OffsetDateTime,
  ) {
    logger.info("data was installed successfully")

    appDB.withTransaction(target, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.getWarningsSequence().joinToString("\n").takeUnless(String::isEmpty)
      ))
    }
  }

  private fun InstallationContext.WithPlugin.handleValidationFailureResponse(
    res:              ValidationErrorResponse,
    updatedTimestamp: OffsetDateTime,
  ) {
    logger.info("installation failed due to validation errors")

    appDB.withTransaction(target, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        res.getWarningsSequence().joinToString("\n")
      ))
    }
  }

  private fun InstallationContext.WithPlugin.handleMissingDependenciesResponse(
    res:              MissingDependencyResponse,
    updatedTimestamp: OffsetDateTime,
  ) {
    logger.info("installation rejected for missing dependencies")

    appDB.withTransaction(target, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        res.body.warnings.joinToString("\n")
      ))
    }
  }

  private fun InstallationContext.WithPlugin.handleScriptErrorResponse(
    res: ScriptErrorResponse,
    updatedTimestamp: OffsetDateTime,
  ) {
    logger.error("installation failed due to plugin script error: {}", res.message)

    appDB.withTransaction(target, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(plugin.name, target, ownerID, datasetID, res.message)
  }

  private fun InstallationContext.WithPlugin.handleServerErrorResponse(
    res:              ServerErrorResponse,
    updatedTimestamp: OffsetDateTime,
  ) {
    logger.error("installation failed due to plugin server error: {}", res.message)

    appDB.withTransaction(target, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(plugin.name, target, ownerID, datasetID, res.message)
  }

  context(ctx: InstallationContext)
  private suspend fun <T> DatasetDirectory.withInstallFiles(
    fn: suspend (
      meta:      InputStream,
      manifest:  InputStream,
      upload:    InputStream,
      propFiles: Iterable<DataPropertiesFile>,
    ) -> T
  ) =
    getMetaFile().open()!!.use { meta ->
      getManifestFile().open()!!.use { manifest ->
        getInstallReadyFile().open()!!.use { data ->
          val ogFiles = getDataPropertiesFiles().toMutableList()
          val dataPropFiles = ArrayList<DataPropertiesFile>(ogFiles.size)

          ogFiles.forEach {
            try { dataPropFiles.add(DataPropertiesFile(it.baseName, it.open()!!)) }
            catch (e: Throwable) {
              dataPropFiles.closeAll()
              throw e
            }
          }
          ogFiles.clear()

          try {
            fn(meta, manifest, data, dataPropFiles)
          } finally {
            dataPropFiles.closeAll()
          }
        }
      }
    }

  context(ctx: InstallationContext)
  private fun List<DataPropertiesFile>.closeAll() {
    forEach {
      try { it.close() }
      catch (e: Throwable) {
        ctx.logger.error("failed to close object stream for {}", it.name, e)
      }
    }
  }

  private fun InstallationContext.tryMarkRevised(meta: DatasetMetadata) {
    meta.revisionHistory!!.revisions
      .asSequence()
      .sortedByDescending { it.timestamp }
      .map { store.getDatasetDirectory(ownerID, it.revisionID) }
      .forEach { tryMarkRevised(it) }

    tryMarkRevised(store.getDatasetDirectory(ownerID, meta.revisionHistory!!.originalID))
  }

  private fun tryMarkRevised(dir: DatasetDirectory) {
    dir.getRevisedFlag().also {
      if (!it.exists())
        it.create()
    }
  }
}
