package vdi.module.handler.install.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import org.veupathdb.vdi.lib.db.app.withTransaction
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.withTransaction
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.response.ind.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase
import java.nio.file.Path
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

internal class InstallDataTriggerHandlerImpl(private val config: InstallTriggerHandlerConfig)
  : InstallDataTriggerHandler
  , VDIServiceModuleBase("install-data-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = CacheDB()

  private val appDB = AppDB()

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.installDataTriggerTopic, config.kafkaConsumerConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val wp = WorkerPool("install-data-workers", config.jobQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.Install.queueSize.inc(it.toDouble())
    }

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.installDataTriggerMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received install job for dataset $userID/$datasetID from source $source")
              wp.submit { tryExecute(userID, datasetID, dm) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }

  private fun tryExecute(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    if (datasetsInProgress.add(datasetID)) {
      try {
        executeJob(userID, datasetID, dm)
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      log.info("data installation already in progress for dataset {}/{}, skipping install job", userID, datasetID)
    }
  }

  /**
   * Execute Job 1
   *
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
  private fun executeJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    log.trace("executeJob(userID={}, datasetID={}, dm=...)", userID, datasetID)

    log.debug("looking up dataset directory for user {}, dataset {}", userID, datasetID)
    val dir = dm.getDatasetDirectory(userID, datasetID)

    // if the directory doesn't yet exist in S3, then how did we even get here?
    if (!dir.exists()) {
      log.error("somehow got an install trigger for a dataset whose directory does not exist? dataset {}/{}", userID, datasetID)
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
      log.info("skipping install data event for dataset {}/{}: dataset does not yet have a dataset record in the cache DB", userID, datasetID)
      return
    }

    // If the dataset has been marked as deleted, then we should bail here
    // because we don't process deleted datasets.
    if (cdbDataset.isDeleted) {
      log.info("skipping install data event for dataset {}/{}: dataset has been marked as deleted in the cache DB", userID, datasetID)
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

    val installableFileTimestamp = dir.getInstallReadyTimestamp()
    if (installableFileTimestamp == null) {
      log.error("could not fetch last modified timestamp for installable file for dataset {}/{}", userID, datasetID)
      return
    }

    cacheDB.withTransaction { it.updateDataSyncControl(datasetID, installableFileTimestamp) }

    // For each target project
    meta.projects
      .forEach { projectID ->
        // If the handler doesn't apply to the target project, again we
        // shouldn't be here, but we can bail out now with a warning.
        if (!handler.appliesToProject(projectID)) {
          log.warn("skipping install data event for dataset {}/{}: handler for type {}:{} does not apply to project {}", userID, datasetID, meta.type.name, meta.type.version, projectID)
          return@forEach
        }

        executeJob(userID, datasetID, projectID, dir, handler.client, installableFileTimestamp)
      }
  }

  /**
   * Execute Job 2
   *
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
  private fun executeJob(
    userID:    UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    s3Dir:     DatasetDirectory,
    handler:   PluginHandlerClient,
    installableFileTimestamp: OffsetDateTime,
  ) {
    val appDB   = appDB.accessor(projectID)
    if (appDB == null) {
      log.info("skipping install event for dataset {}/{} into project {} due to the target project being disabled.", userID, datasetID, projectID)
      return
    }

    val dataset = appDB.selectDataset(datasetID)
    if (dataset == null) {
      log.info("skipping install event for dataset {}/{} into project {} due to no dataset record being present", userID, datasetID, projectID)
      return
    }

    if (dataset.isDeleted != DeleteFlag.NotDeleted) {
      log.info("skipping install event for dataset {}/{} into project {} due to the dataset being marked as deleted", userID, datasetID, projectID)
      return
    }

    val timer = Metrics.Install.duration.labels(dataset.typeName, dataset.typeVersion).startTimer()

    try {
      val status = appDB.selectDatasetInstallMessage(datasetID, InstallType.Data)

      if (status == null) {
        var race = false
        this.appDB.withTransaction(projectID) {
          try {
            it.insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Data, InstallStatus.Running, null))
          } catch (e: SQLException) {
            // Error code 1 == unique constraint violation: https://docs.oracle.com/en/database/oracle/oracle-database/19/errmg/ORA-00000.html
            if (e.errorCode == 1) {
              log.info("Unique constraint violation when writing install data message for dataset {}/{}, assuming race condition and ignoring.", userID, datasetID)
              race = true
            } else {
              throw e
            }
          }
        }

        if (race) {
          return
        }
      } else {
        if (status.status != InstallStatus.ReadyForReinstall) {
          log.info("Skipping install event for dataset {}/{} into project {} due to the dataset status being {}", userID, datasetID, projectID, status.status)
          return
        }
      }

      // FIXME: MOVE THIS OUTSIDE OF THIS FUNCTION, THIS JUST REDOWNLOADS THE
      //        FILE FOR EVERY TARGET PROJECT!
      val response = withDataTar(s3Dir) { dataTar ->
        dataTar.inputStream()
          .use { inp -> handler.postInstallData(datasetID, projectID, inp) }
      }

      Metrics.Install.count.labels(dataset.typeName, dataset.typeVersion, response.responseCode.toString()).inc()

      when (response.type) {
        InstallDataResponseType.Success
        -> handleSuccessResponse(response as InstallDataSuccessResponse, userID, datasetID, projectID, installableFileTimestamp)

        InstallDataResponseType.BadRequest
        -> handleBadRequestResponse(response as InstallDataBadRequestResponse, userID, datasetID, projectID, installableFileTimestamp)

        InstallDataResponseType.ValidationFailure
        -> handleValidationFailureResponse(
          response as InstallDataValidationFailureResponse,
          userID,
          datasetID,
          projectID,
          installableFileTimestamp
        )

        InstallDataResponseType.MissingDependencies
        -> handleMissingDependenciesResponse(
          response as InstallDataMissingDependenciesResponse,
          userID,
          datasetID,
          projectID,
          installableFileTimestamp
        )

        InstallDataResponseType.UnexpectedError
        -> handleUnexpectedErrorResponse(response as InstallDataUnexpectedErrorResponse, userID, datasetID, projectID, installableFileTimestamp)
      }
    } finally {
      timer.observeDuration()
    }
  }

  /**
   * Executes the given function in the context of a temporary directory with a
   * `tar.gz` containing the necessary files for a dataset installation passed
   * to the function.
   *
   * On completion of this function call, the temporary directory and tar file
   * will be deleted.
   *
   * @param s3Dir `DatasetDirectory` instance used to download the data files
   * for the target dataset into the temp directory to be packaged into the tar
   * file that will be passed to the given function.
   *
   * @param fn Function that will be called and passed the packaged tar file.
   *
   * @param T The return type for the value returned by the given function.
   *
   * @return The value returned by the given function.
   */
  private fun <T> withDataTar(s3Dir: DatasetDirectory, fn: (Path) -> T): T {
    TempFiles.withTempDirectory { tempDir ->
      val tarFile      = tempDir.resolve("dataset.tar.gz")
      val metaFile     = tempDir.resolve(DatasetMetaFilename)
      val manifestFile = tempDir.resolve(DatasetManifestFilename)
      val files        = mutableListOf(metaFile, manifestFile)

      metaFile.outputStream()
        .use { out -> s3Dir.getMetaFile().loadContents()!!.use { inp -> inp.transferTo(out) } }

      manifestFile.outputStream()
        .use { out -> s3Dir.getManifestFile().loadContents()!!.use { inp -> inp.transferTo(out) } }

      s3Dir.getInstallReadyFile()
        .also { ddf ->
          val zip = tempDir.resolve(ddf.baseName)
          zip.outputStream()
            .use { out -> ddf.loadContents()!!.use { inp -> inp.transferTo(out) } }

          Zip.zipEntries(zip).forEach { (entry, inp) ->
            val file = tempDir.resolve(entry.name)
            log.debug("repacking file {} into {}", file, tarFile)
            file.outputStream()
              .use { out -> inp.transferTo(out) }
            files.add(file)
          }
        }

      Tar.compressWithGZip(tarFile, files)

      return fn(tarFile)
    }
  }

  private fun handleSuccessResponse(
    res: InstallDataSuccessResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info("dataset {}/{} data was installed successfully into project {}", userID, datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.warnings.takeUnless { it.isEmpty() }?.joinToString("\n")
      ))
    }
  }

  private fun handleBadRequestResponse(
    res: InstallDataBadRequestResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error("dataset {}/{} install into {} failed due to bad request exception from handler server: {}", userID, datasetID, projectID, res.message)

    appDB.withTransaction(projectID) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw Exception(res.message)
  }

  private fun handleValidationFailureResponse(
    res: InstallDataValidationFailureResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info("dataset {}/{} install into {} failed due to validation error", userID, datasetID, projectID)

    appDB.withTransaction(projectID) {
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
    res: InstallDataMissingDependenciesResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.info("dataset {}/{} install into {} was rejected for missing dependencies", userID, datasetID, projectID)

    appDB.withTransaction(projectID) {
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
    res: InstallDataUnexpectedErrorResponse,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    updatedTimestamp: OffsetDateTime,
  ) {
    log.error("dataset {}/{} install into {} failed with a 500 from the handler server", userID, datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateSyncControlDataTimestamp(datasetID, updatedTimestamp)

      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw Exception(res.message)
  }
}
