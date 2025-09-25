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
import vdi.core.plugin.client.response.ind.*
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
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
            .forEach { (userID, datasetID, source) ->
              log.info("received install job for dataset $userID/$datasetID from source $source")
              wp.submit { tryInstallData(userID, datasetID, dm) }
            }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private suspend fun tryInstallData(userID: UserID, datasetID: DatasetID, dm: DatasetObjectStore) {
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
    val handler = PluginHandlers[meta.type]

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

      val success = dir.buildInstallBundle { metaStream, manifestStream, uploadStream ->
        meta.installTargets
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

            handler.installData(
              userID,
              datasetID,
              projectID,
              installableFileTimestamp,
              metaStream,
              manifestStream,
              uploadStream,
            )
          }
      }

      if (success && meta.revisionHistory != null)
        tryMarkRevised(userID, meta, dm)
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
  private suspend fun PluginHandler.installData(
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    installableFileTimestamp: OffsetDateTime,
    meta: InputStream,
    manifest: InputStream,
    data: InputStream,
  ): Boolean {
    var timer: Histogram.Timer? = null

    try {
      val appDB = appDB.accessor(installTarget, type) orElse {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to the target project being disabled.",
          userID,
          datasetID,
          installTarget
        )
        return false
      }

      val dataset = appDB.selectDataset(datasetID) orElse  {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to no dataset record being present",
          userID,
          datasetID,
          installTarget
        )
        return false
      }

      if (dataset.deletionState != DeleteFlag.NotDeleted) {
        log.info(
          "skipping install event for dataset {}/{} into project {} due to the dataset being marked as deleted",
          userID,
          datasetID,
          installTarget
        )
        return false
      }

      val status = appDB.selectDatasetInstallMessage(datasetID, InstallType.Data)

      timer = Metrics.Install.duration.labels(dataset.type.name.toString(), dataset.type.version).startTimer()

      if (status == null) {
        var race = false
        this@InstallDataLaneImpl.appDB.withTransaction(installTarget, type) {
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
            installTarget,
            status.status
          )
          return false
        }
      }

      val response = try {
        client.postInstallData(datasetID, installTarget, meta, manifest, data)
      } catch (e: S34KError) { // Don't mix up minio errors with request errors.
        throw PluginException.installData(name, installTarget, userID, datasetID, cause = e)
      } catch (e: Throwable) {
        throw PluginRequestException.installData(name, installTarget, userID, datasetID, cause = e)
      }

      Metrics.Install.count.labels(dataset.type.name.toString(), dataset.type.version, response.responseCode.toString()).inc()

      return when (response.type) {
        InstallDataResponseType.Success -> {
          handleSuccessResponse(
            response as InstallDataSuccessResponse,
            userID,
            datasetID,
            installTarget,
            installableFileTimestamp,
          )
          true
        }

        InstallDataResponseType.BadRequest -> {
          handleBadRequestResponse(
            response as InstallDataBadRequestResponse,
            userID,
            datasetID,
            installTarget,
            installableFileTimestamp,
          )
          false
        }

        InstallDataResponseType.ValidationFailure -> {
          handleValidationFailureResponse(
            response as InstallDataValidationFailureResponse,
            userID,
            datasetID,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        InstallDataResponseType.MissingDependencies -> {
          handleMissingDependenciesResponse(
            response as InstallDataMissingDependenciesResponse,
            userID,
            datasetID,
            installTarget,
            installableFileTimestamp
          )
          false
        }

        InstallDataResponseType.UnexpectedError -> {
          handleUnexpectedErrorResponse(
            response as InstallDataUnexpectedErrorResponse,
            userID,
            datasetID,
            installTarget,
            installableFileTimestamp
          )
          false
        }
      }
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installData(name, installTarget, userID, datasetID, cause = e)
    } finally {
      timer?.observeDuration()
    }
  }

  private fun PluginHandler.handleSuccessResponse(
    res: InstallDataSuccessResponse,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} data was installed successfully into project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      name,
    )

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.warnings.takeUnless(Collection<String>::isEmpty)?.joinToString("\n")
      ))
    }
  }

  private fun PluginHandler.handleBadRequestResponse(
    res: InstallDataBadRequestResponse,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error(
      "dataset {}/{} install into {} failed due to bad request exception from plugin {}: {}",
      userID,
      datasetID,
      installTarget,
      name,
      res.message,
    )

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(name, installTarget, userID, datasetID, res.message)
  }

  private fun PluginHandler.handleValidationFailureResponse(
    res: InstallDataValidationFailureResponse,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} install into {} via plugin {} failed due to validation error",
      userID,
      datasetID,
      installTarget,
      this,
    )

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        res.warnings.joinToString("\n")
      ))
    }
  }

  private fun PluginHandler.handleMissingDependenciesResponse(
    res: InstallDataMissingDependenciesResponse,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info(
      "dataset {}/{} install into {} was rejected by plugin {} for missing dependencies",
      userID,
      datasetID,
      installTarget,
      name,
    )

    appDB.withTransaction(installTarget, type) {
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
  private fun PluginHandler.handleUnexpectedErrorResponse(
    res: InstallDataUnexpectedErrorResponse,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error(
      "dataset {}/{} install into {} failed with a 500 from plugin {}",
      userID,
      datasetID,
      installTarget,
      name,
    )

    appDB.withTransaction(installTarget, type) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw PluginException.installData(name, installTarget, userID, datasetID, res.message)
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

  private fun tryMarkRevised(ownerID: UserID, meta: DatasetMetadata, dm: DatasetObjectStore) {
    meta.revisionHistory!!.revisions
      .asSequence()
      .sortedByDescending { it.timestamp }
      .map { dm.getDatasetDirectory(ownerID, it.revisionID) }
      .forEach { tryMarkRevised(it) }

    tryMarkRevised(dm.getDatasetDirectory(ownerID, meta.revisionHistory!!.originalID))
  }

  private fun tryMarkRevised(dir: DatasetDirectory) {
    dir.getRevisedFlag().also {
      if (!it.exists())
        it.touch()
    }
  }
}
