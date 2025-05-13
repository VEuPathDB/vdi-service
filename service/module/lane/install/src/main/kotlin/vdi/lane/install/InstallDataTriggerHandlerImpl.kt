package vdi.lane.install

import io.prometheus.client.Histogram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.lib.s3.s34k.errors.S34KError
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.util.or
import java.io.InputStream
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.lib.async.WorkerPool
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.isUniqueConstraintViolation
import vdi.lib.db.app.model.DatasetInstallMessage
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.withTransaction
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.withTransaction
import vdi.lib.kafka.router.KafkaRouter
import vdi.lib.logging.logger
import vdi.lib.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.ind.*
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers
import vdi.lib.s3.DatasetDirectory
import vdi.lib.s3.DatasetObjectStore

internal class InstallDataTriggerHandlerImpl(private val config: InstallTriggerHandlerConfig, abortCB: AbortCB)
  : InstallDataTriggerHandler
  , AbstractVDIModule("install-data", abortCB, logger<InstallDataTriggerHandler>())
{
  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.consumerConfig)
    val kr = requireKafkaRouter(config.producerConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val wp = WorkerPool("sync-data", config.jobQueueSize, config.workerPoolSize) {
      Metrics.Install.queueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received install job for dataset $userID/$datasetID from source $source")
              wp.submit { tryInstallData(userID, datasetID, dm, kr) }
            }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun tryInstallData(userID: UserID, datasetID: DatasetID, dm: DatasetObjectStore, kr: KafkaRouter) {
    if (datasetsInProgress.add(datasetID)) {
      try {
        installData(userID, datasetID, dm)
      } catch (e: PluginException) {
        e.log(log::error)
      } catch (e: Throwable) {
        PluginException.installData("N/A", "N/A", userID, datasetID, cause = e).log(log::error)
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      log.info("data installation already in progress for dataset {}/{}, ignoring install event", userID, datasetID)
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
   *
   * @param userID ID of the owning user for the target dataset.
   *
   * @param datasetID ID of the target dataset to install.
   *
   * @param dm `DatasetManager` instance that will be used to look up the
   * dataset's files in S3.
   */
  private suspend fun installData(userID: UserID, datasetID: DatasetID, dm: DatasetObjectStore) {
    log.debug("looking up dataset directory for {}/{}", userID, datasetID)

    val dir = dm.getDatasetDirectory(userID, datasetID)

    // if the directory doesn't yet exist in S3, then how did we even get here?
    if (!dir.exists()) {
      log.error("received an install trigger for a dataset whose minio directory does not exist: {}/{}", userID, datasetID)
      return
    }

    // Lookup the sync control record in the postgres DB
    val syncControl = cacheDB.selectSyncControl(datasetID)

    // If the sync control record was not found in postgres, then we are likely
    // seeing this event due to a cross campus S3 sync, and the metadata JSON
    // file hasn't landed yet.
    if (syncControl == null) {
      log.info("skipping install data event for dataset {}/{}: dataset does not yet have a sync control record in the cache DB", userID, datasetID)
      return
    }

    // Lookup the dataset record in the postgres DB
    val cdbDataset = cacheDB.selectDataset(datasetID)

    // If the dataset record doesn't yet exist in postgres DB then we caught
    // this event _while_ the metadata JSON event was being handled due to a
    // cross campus S3 sync, and we aren't yet ready for an install event.
    if (cdbDataset == null) {
      log.info("skipping install data event for dataset {}/{}: dataset does not yet have a record in the cache DB", userID, datasetID)
      return
    }

    // If the dataset has been marked as deleted, then we should bail here
    // because we don't process deleted datasets.
    if (cdbDataset.isDeleted) {
      log.info("skipping install data event for dataset {}/{}: dataset has been marked as deleted in cache DB", userID, datasetID)
      return
    }

    // If the dataset is not yet import complete then we caught this event due
    // to the import process actively happening, bail out here.
    if (!dir.hasInstallReadyFile()) {
      log.info("skipping install data event for dataset {}/{}: dataset is not yet import complete", userID, datasetID)
      return
    }

    // Load the metadata so that we can iterate through the target projects.
    val meta = dir.getMetaFile().load()!!

    // Get a handler instance for the target dataset type.
    val handler = PluginHandlers[meta.type.name, meta.type.version]

    // If there is no handler for the target type, we shouldn't have gotten
    // here, but we can bail now to prevent issues.
    if (handler == null) {
      log.error("skipping install data event for dataset {}/{}: no handler configured for dataset type {}:{}", userID, datasetID, meta.type.name, meta.type.version)
      return
    }

    try {
      val installableFileTimestamp = dir.getInstallReadyTimestamp()
      if (installableFileTimestamp == null) {
        log.error("could not fetch last modified timestamp for installable file for dataset {}/{}", userID, datasetID)
        return
      }

      cacheDB.withTransaction { it.updateDataSyncControl(datasetID, installableFileTimestamp) }

      // For each target project
      val success = meta.projects
        .all { projectID ->
          // If the handler doesn't apply to the target project, again we
          // shouldn't be here, but we can bail out now with a warning.
          if (!handler.appliesToProject(projectID)) {
            log.warn(
              "skipping install data event for dataset {}/{}: handler for type {}:{} does not apply to project {}",
              userID,
              datasetID,
              meta.type.name,
              meta.type.version,
              projectID,
            )
            return@all true
          }

          installData(userID, datasetID, projectID, dir, handler, installableFileTimestamp)
        }

      if (success && meta.revisionHistory.isNotEmpty())
        tryMarkRevised(userID, meta, dm)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(handler.displayName, "N/A", userID, datasetID, cause = e)
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
  private suspend fun installData(
    userID:    UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    s3Dir: DatasetDirectory,
    handler: PluginHandler,
    installableFileTimestamp: OffsetDateTime,
  ): Boolean {
    var timer: Histogram.Timer? = null

    try {
      val appDB = appDB.accessor(projectID, handler.type) or {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to the target project being disabled.",
          userID,
          datasetID,
          projectID
        )
        return false
      }

      val dataset = appDB.selectDataset(datasetID) or {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to no dataset record being present",
          userID,
          datasetID,
          projectID
        )
        return false
      }

      if (dataset.deletionState != DeleteFlag.NotDeleted) {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to the dataset being marked as deleted",
          userID,
          datasetID,
          projectID
        )
        return false
      }

      val status = appDB.selectDatasetInstallMessage(datasetID, InstallType.Data)

      timer = Metrics.Install.duration.labels(dataset.typeName.toString(), dataset.typeVersion).startTimer()

      if (status == null) {
        var race = false
        this.appDB.withTransaction(projectID, handler.type) {
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
              log.info(
                "Unique constraint violation when writing install data message for dataset {}/{}, assuming race condition and ignoring.",
                userID,
                datasetID
              )
              race = true
            } else {
              throw e
            }
          }
        }

        if (race) {
          return false
        }
      } else {
        if (status.status != InstallStatus.ReadyForReinstall) {
          log.info(
            "Skipping install event for dataset {}/{} into project {} due to the dataset status being {}",
            userID,
            datasetID,
            projectID,
            status.status
          )
          return false
        }
      }

      // FIXME: MOVE THE ZIP CREATION OUTSIDE OF THE PROJECT LOOP TO AVOID
      //        RECREATING IT FOR EACH TARGET.
      val response = try {
        withInstallBundle(s3Dir) { meta, manifest, data ->
          handler.client.postInstallData(datasetID, projectID, meta, manifest, data)
        }
      } catch (e: S34KError) { // Don't mix up minio errors with request errors.
        throw PluginException.installData(handler.displayName, projectID, userID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.installData(handler.displayName, projectID, userID, datasetID, cause = e)
      }

      Metrics.Install.count.labels(dataset.typeName.toString(), dataset.typeVersion, response.responseCode.toString()).inc()

      return when (response.type) {
        InstallDataResponseType.Success
        -> {
          handleSuccessResponse(
            handler,
            response as InstallDataSuccessResponse,
            userID,
            datasetID,
            projectID,
            installableFileTimestamp,
          )
          true
        }

        InstallDataResponseType.BadRequest
        -> {
          handleBadRequestResponse(
            handler,
            response as InstallDataBadRequestResponse,
            userID,
            datasetID,
            projectID,
            installableFileTimestamp,
          )
          false
        }

        InstallDataResponseType.ValidationFailure
        -> {
          handleValidationFailureResponse(
            handler,
            response as InstallDataValidationFailureResponse,
            userID,
            datasetID,
            projectID,
            installableFileTimestamp
          )
          false
        }

        InstallDataResponseType.MissingDependencies
        -> {
          handleMissingDependenciesResponse(
            handler,
            response as InstallDataMissingDependenciesResponse,
            userID,
            datasetID,
            projectID,
            installableFileTimestamp
          )
          false
        }

        InstallDataResponseType.UnexpectedError
        -> {
          handleUnexpectedErrorResponse(
            handler,
            response as InstallDataUnexpectedErrorResponse,
            userID,
            datasetID,
            projectID,
            installableFileTimestamp
          )
          false
        }
      }
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(handler.displayName, projectID, userID, datasetID, cause = e)
    } finally {
      timer?.observeDuration()
    }
  }

  private fun handleSuccessResponse(
    handler: PluginHandler,
    res: InstallDataSuccessResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} data was installed successfully into project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName,
    )

    appDB.withTransaction(projectID, handler.type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.warnings.takeUnless(Collection<String>::isEmpty)?.joinToString("\n")
      ))
    }
  }

  private fun handleBadRequestResponse(
    handler: PluginHandler,
    res: InstallDataBadRequestResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error(
      "dataset {}/{} install into {} failed due to bad request exception from plugin {}: {}",
      userID,
      datasetID,
      projectID,
      handler.displayName,
      res.message,
    )

    appDB.withTransaction(projectID, handler.type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(handler.displayName, projectID, userID, datasetID, res.message)
  }

  private fun handleValidationFailureResponse(
    handler: PluginHandler,
    res: InstallDataValidationFailureResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} install into {} via plugin {} failed due to validation error",
      userID,
      datasetID,
      projectID,
      handler,
    )

    appDB.withTransaction(projectID, handler.type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        res.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleMissingDependenciesResponse(
    handler: PluginHandler,
    res: InstallDataMissingDependenciesResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} install into {} was rejected by plugin {} for missing dependencies",
      userID,
      datasetID,
      projectID,
      handler.displayName,
    )

    appDB.withTransaction(projectID, handler.type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        res.warnings.joinToString("\n")
      ))
    }
  }

  /**
   * Handles an unexpected error response from the handler service.
   */
  private fun handleUnexpectedErrorResponse(
    handler: PluginHandler,
    res: InstallDataUnexpectedErrorResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error(
      "dataset {}/{} install into {} failed with a 500 from plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName,
    )

    appDB.withTransaction(projectID, handler.type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(handler.displayName, projectID, userID, datasetID, res.message)
  }

  private suspend fun <T> withInstallBundle(
    s3Dir: DatasetDirectory,
    fn: suspend (meta: InputStream, manifest: InputStream, upload: InputStream) -> T
  ) =
    s3Dir.getMetaFile().loadContents()!!.use { meta ->
      s3Dir.getManifestFile().loadContents()!!.use { manifest ->
        s3Dir.getInstallReadyFile().loadContents()!!.use { data ->
          fn(meta, manifest, data)
        }
      }
    }

  private fun tryMarkRevised(ownerID: UserID, meta: VDIDatasetMeta, dm: DatasetObjectStore) {
    meta.revisionHistory.asSequence()
      .sortedByDescending { it.timestamp }
      .drop(1) // skip the newest/current revision
      .map { dm.getDatasetDirectory(ownerID, it.revisionID) }
      .forEach { tryMarkRevised(it) }

    tryMarkRevised(dm.getDatasetDirectory(ownerID, meta.originalID!!))
  }

  private fun tryMarkRevised(dir: DatasetDirectory) {
    dir.getRevisedFlag().also {
      if (!it.exists())
        it.touch()
    }
  }
}
