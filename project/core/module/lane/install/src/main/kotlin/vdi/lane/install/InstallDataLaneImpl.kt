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
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.util.orElse
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.MissingDependencyResponse
import vdi.core.plugin.client.response.ScriptErrorResponse
import vdi.core.plugin.client.response.ServerErrorResponse
import vdi.core.plugin.client.response.StreamResponse
import vdi.core.plugin.client.response.ValidationResponse
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.model.data.*

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
      Metrics.Install.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .forEach { msg ->
              wp.submit {
                InstallationContext(msg, dm, logger)
                  .also { it.logger.info("received install job from source {}", msg.eventSource) }
                  .tryInstallData()
              }
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
        PluginException.installData("N/A", "N/A", userID, datasetID, cause = e).log(logger::error)
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

    val dir = store.getDatasetDirectory(userID, datasetID)

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
      logger.error("skipping install data event; no handler configured for dataset type {}:{}", meta.type.name, meta.type.version)
      return
    }

    try {
      val installableFileTimestamp = dir.getInstallReadyTimestamp()
      if (installableFileTimestamp == null) {
        logger.error("could not fetch last modified timestamp for installable file")
        return
      }

      cacheDB.withTransaction { it.updateDataSyncControl(datasetID, installableFileTimestamp) }

      val success = dir.buildInstallBundle { metaStream, manifestStream, uploadStream ->
        meta.installTargets
          .all { projectID ->
            // If the handler doesn't apply to the target project, again we
            // shouldn't be here, but we can bail out now with a warning.
            if (!handler.appliesToProject(projectID)) {
              logger.warn(
                "skipping install data event; handler for type {}:{} does not apply to project {}",
                meta.type.name,
                meta.type.version,
                projectID,
              )
              return@all true
            }

            handler.installData(
              projectID,
              installableFileTimestamp,
              metaStream,
              manifestStream,
              uploadStream,
            )
          }
      }

      if (success && meta.revisionHistory != null)
        tryMarkRevised(meta)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(handler.name, "N/A", userID, datasetID, cause = e)
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
  context(ctx: InstallationContext)
  private suspend fun PluginHandler.installData(
    installTarget: InstallTargetID,
    installableFileTimestamp: OffsetDateTime,
    meta: InputStream,
    manifest: InputStream,
    data: InputStream,
  ): Boolean {
    var timer: Histogram.Timer? = null

    try {
      val appDB = appDB.accessor(installTarget, type) orElse {
        ctx.logger.info("skipping install event into project {}; target project is disabled.", installTarget)
        return false
      }

      val dataset = appDB.selectDataset(ctx.datasetID) orElse  {
        ctx.logger.info("skipping install event into project {}; no dataset record is present", installTarget)
        return false
      }

      if (dataset.deletionState != DeleteFlag.NotDeleted) {
        ctx.logger.info("skipping install event into project {}; dataset marked as deleted", installTarget)
        return false
      }

      val status = appDB.selectDatasetInstallMessage(ctx.datasetID, InstallType.Data)

      timer = Metrics.Install.duration.labels(dataset.type.name.toString(), dataset.type.version).startTimer()

      if (status == null) {
        var race = false
        this@InstallDataLaneImpl.appDB.withTransaction(installTarget, type) {
          try {
            it.insertDatasetInstallMessage(
              DatasetInstallMessage(
                ctx.datasetID,
                InstallType.Data,
                InstallStatus.Running,
                null
              )
            )
          } catch (e: SQLException) {
            if (it.isUniqueConstraintViolation(e)) {
              ctx.logger.info("unique constraint violation when writing install data message; assuming race condition and ignoring")
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
          ctx.logger.info("skipping install event into project {}; dataset status is {}", installTarget, status.status)
          return false
        }
      }

      val response = try {
        client.postInstallData(ctx.eventID, ctx.datasetID, installTarget, meta, manifest, data)
      } catch (e: S34KError) { // Don't mix up minio errors with request errors.
        throw PluginException.installData(name, installTarget, ctx.userID, ctx.datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.installData(name, installTarget, ctx.userID, ctx.datasetID, cause = e)
      }

      Metrics.Install.count.labels(dataset.type.name.toString(), dataset.type.version, response.status.code.toString()).inc()

      return when (response.status) {
        PluginResponseStatus.Success -> {
          handleSuccessResponse(
            response as ValidationResponse,
            installTarget,
            installableFileTimestamp,
          )
          true
        }

        PluginResponseStatus.ValidationError -> {
          handleValidationFailureResponse(
            response as ValidationResponse,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        PluginResponseStatus.MissingDependencyError -> {
          handleMissingDependenciesResponse(
            response as MissingDependencyResponse,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        PluginResponseStatus.ScriptError -> {
          handleScriptErrorResponse(
            response as ScriptErrorResponse,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        PluginResponseStatus.ServerError -> {
          handleServerErrorResponse(
            response as ServerErrorResponse,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        else -> {
          if (response is StreamResponse)
            response.body.close()

          throw IllegalStateException("illegal response status ${response.status}")
        }
      }
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(name, installTarget, ctx.userID, ctx.datasetID, cause = e)
    } finally {
      timer?.observeDuration()
    }
  }

  context(ctx: InstallationContext)
  private fun PluginHandler.handleSuccessResponse(
    res:              ValidationResponse,
    installTarget:    InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    ctx.logger.info("data was installed successfully into project {} via plugin {}", installTarget, name)

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(ctx.datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        ctx.datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.getWarningsSequence().joinToString("\n").takeUnless(String::isEmpty)
      ))
    }
  }

  context(ctx: InstallationContext)
  private fun PluginHandler.handleValidationFailureResponse(
    res:              ValidationResponse,
    installTarget:    InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    ctx.logger.info(
      "installation into {} via plugin {} failed due to validation errors",
      installTarget,
      this,
    )

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(ctx.datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        ctx.datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        res.getWarningsSequence().joinToString("\n")
      ))
    }
  }

  context(ctx: InstallationContext)
  private fun PluginHandler.handleMissingDependenciesResponse(
    res:              MissingDependencyResponse,
    installTarget:    InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    ctx.logger.info("installation into {} was rejected by plugin {} for missing dependencies", installTarget, name)

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(ctx.datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        ctx.datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        res.body.warnings.joinToString("\n")
      ))
    }
  }

  context(ctx: InstallationContext)
  private fun PluginHandler.handleScriptErrorResponse(
    res: ScriptErrorResponse,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    ctx.logger.error("installation into {} failed due to plugin script error from plugin {}", installTarget, name)

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(ctx.datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        ctx.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(name, installTarget, ctx.userID, ctx.datasetID, res.message)
  }

  context(ctx: InstallationContext)
  private fun PluginHandler.handleServerErrorResponse(
    res:              ServerErrorResponse,
    installTarget:    InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    ctx.logger.error("installation into {} failed due to plugin server error from plugin {}", installTarget, name)

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(ctx.datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        ctx.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(name, installTarget, ctx.userID, ctx.datasetID, res.message)
  }

  private suspend fun <T> DatasetDirectory.buildInstallBundle(
    fn: suspend (meta: InputStream, manifest: InputStream, upload: InputStream) -> T
  ) =
    getMetaFile().loadContents()!!.use { meta ->
      getManifestFile().loadContents()!!.use { manifest ->
        getInstallReadyFile().loadContents()!!.use { data ->
          fn(meta, manifest, data)
        }
      }
    }

  private fun InstallationContext.tryMarkRevised(meta: DatasetMetadata) {
    meta.revisionHistory!!.revisions
      .asSequence()
      .sortedByDescending { it.timestamp }
      .map { store.getDatasetDirectory(userID, it.revisionID) }
      .forEach { tryMarkRevised(it) }

    tryMarkRevised(store.getDatasetDirectory(userID, meta.revisionHistory!!.originalID))
  }

  private fun tryMarkRevised(dir: DatasetDirectory) {
    dir.getRevisedFlag().also {
      if (!it.exists())
        it.touch()
    }
  }
}
