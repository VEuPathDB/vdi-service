package vdi.module.handler.install.data

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.response.ind.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.InstallTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.lang.IllegalStateException
import java.nio.file.Path
import java.time.OffsetDateTime
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.module.handler.install.data.config.InstallTriggerHandlerConfig

internal class InstallDataTriggerHandlerImpl(private val config: InstallTriggerHandlerConfig) : InstallDataTriggerHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  override suspend fun start() {
    if (!started) {
      log.info("starting install-data-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering install-data-trigger-handler module shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val kc = requireKafkaConsumer()
    val dm = DatasetManager(requireS3Bucket(requireS3Client()))
    val wp = WorkerPool("install-data-workers", config.workerPoolSize.toInt(), config.jobQueueSize.toInt())

    runBlocking {
      launch(Dispatchers.IO) {
        while (!shutdownTrigger.isTriggered()) {
          kc.selectInstallTriggers()
            .forEach { (userID, datasetID) ->
              log.debug("submitting job to install-data worker pool for user {}, dataset {}", userID, datasetID)
              wp.submit { executeJob(userID, datasetID, dm) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    shutdownConfirm.trigger()
  }

  private suspend inline fun <T> safeExec(err: String, fn: () -> T): T =
    try {
      fn()
    } catch(e: Throwable) {
      log.error(err, e)
      stop()
      throw e
    }

  private suspend fun requireKafkaConsumer() = safeExec("failed to create KafkaConsumer instance") {
    KafkaConsumer(config.installDataTriggerTopic, config.kafkaConsumerConfig)
  }

  private suspend fun requireS3Client() = safeExec("failed to create S3 client instance") {
    S3Api.newClient(config.s3Config)
  }

  private suspend fun requireS3Bucket(s3: S3Client) = safeExec("failed to lookup target S3 bucket") {
    s3.buckets[config.s3Bucket] ?: throw IllegalStateException("bucket ${config.s3Bucket} does not exist!")
  }

  private fun KafkaConsumer.selectInstallTriggers() =
    receive()
      .asSequence()
      .filter {
        if (it.key == config.installDataTriggerMessageKey) {
          true
        } else {
          log.warn("filtering out message with key {} as it does not match expected key {}", it.key, config.installDataTriggerMessageKey)
          false
        }
      }
      .map {
        try {
          JSON.readValue<InstallTrigger>(it.value)
        } catch (e: Throwable) {
          log.warn("received an invalid message body from Kafka: {}", it)
          null
        }
      }
      .filterNotNull()

  private fun executeJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    log.trace("executeJob(userID={}, datasetID={}, dm=...)", userID, datasetID)

    log.debug("looking up dataset directory for user {}, dataset {}", userID, datasetID)
    val dir = dm.getDatasetDirectory(userID, datasetID)

    // if the directory doesn't yet exist in S3, then how did we even get here?
    if (!dir.exists()) {
      log.error("somehow got an install trigger for a dataset whose directory does not exist? dataset {}, user {}", datasetID, userID)
      return
    }

    // Lookup the sync control record in the postgres DB
    val syncControl = CacheDB.selectSyncControl(datasetID)

    // If the sync control record was not found in postgres, then we are likely
    // seeing this event due to a cross campus S3 sync, and the meta.json file
    // hasn't landed yet.
    if (syncControl == null) {
      log.info("skipping install data event for dataset {} (user {}): dataset does not yet have a sync control record in the cache DB", datasetID, userID)
      return
    }

    // Lookup the dataset record in the postgres DB
    val cdbDataset = CacheDB.selectDataset(datasetID)

    // If the dataset record doesn't yet exist in postgres DB then we caught
    // this event _while_ the meta.json event was being handled due to a cross
    // campus S3 sync, and we aren't yet ready for an install event.
    if (cdbDataset == null) {
      log.info("skipping install data event for dataset {} (user {}): dataset does not yet have a dataset record in the cache DB", datasetID, userID)
      return
    }

    // If the dataset has been marked as deleted, then we should bail here
    // because we don't process deleted datasets.
    if (cdbDataset.isDeleted) {
      log.info("skipping install data event for dataset {} (user {}): dataset has been marked as deleted in the cache DB", datasetID, userID)
      return
    }

    // If the dataset is not yet import complete then we caught this event due
    // to the import process actively happening, bail out here.
    if (!dir.isImportComplete()) {
      log.info("skipping install data event for dataset {} (user {}): dataset is not yet import complete", datasetID, userID)
      return
    }

    // Load the metadata so we can iterate through the target projects.
    val meta = dir.getMeta().load()!!

    // Get a handler instance for the target dataset type.
    val handler = PluginHandlers[meta.type.name]

    // If there is no handler for the target type, we shouldn't have gotten
    // here, but we can bail now to prevent issues.
    if (handler == null) {
      log.error("skipping install data event for dataset {} (user {}): no handler configured for dataset type {}", datasetID, userID, meta.type.name)
      return
    }

    // For each target project
    meta.projects
      .forEach { projectID ->
        // If the handler doesn't apply to the target project, again we
        // shouldn't be here, but we can bail out now with a warning.
        if (!handler.appliesToProject(projectID)) {
          log.warn("skipping install data event for dataset {} (user {}): handler for type {} does not apply to project {}", datasetID, userID, meta.type.name, projectID)
          return@forEach
        }

        executeJob(userID, datasetID, projectID, dir, handler.client)
      }
  }

  private fun executeJob(
    userID:    UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    s3Dir:     DatasetDirectory,
    handler:   PluginHandlerClient
  ) {
    log.trace("executeJob(userID={}, datasetID={}, projectID={}, s3Dir=..., handler=...)", userID, datasetID, projectID)

    val appDB   = AppDB.accessor(projectID)
    val dataset = appDB.selectDataset(datasetID)

    if (dataset == null) {
      log.info("skipping install event for dataset {} into project {} due to no dataset record being present", datasetID, projectID)
      return
    }

    if (dataset.isDeleted) {
      log.info("skipping install event for dataset {} into project {} due to the dataset being marked as deleted", datasetID, projectID)
      return
    }

    val status = appDB.selectDatasetInstallMessage(datasetID, InstallType.Data)

    if (status == null) {
      AppDB.withTransaction(projectID) {
        it.insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Data, InstallStatus.Running, null))
      }
    } else {
      if (status.status != InstallStatus.ReadyForReinstall) {
        log.info("skipping install event for dataset {} into project {} due to the dataset status being {}", datasetID, projectID, status.status)
        return
      }
    }

    val response = withDataTar(s3Dir) { dataTar ->
      dataTar.inputStream()
        .use { inp -> handler.postInstallData(datasetID, projectID, inp) }
    }

    when (response.type) {
      InstallDataResponseType.Success             -> handleSuccessResponse(response as InstallDataSuccessResponse, datasetID, projectID)
      InstallDataResponseType.BadRequest          -> handleBadRequestResponse(response as InstallDataBadRequestResponse, datasetID, projectID)
      InstallDataResponseType.ValidationFailure   -> handleValidationFailureResponse(response as InstallDataValidationFailureResponse, datasetID, projectID)
      InstallDataResponseType.MissingDependencies -> handleMissingDependenciesResponse(response as InstallDataMissingDependenciesResponse, datasetID, projectID)
      InstallDataResponseType.UnexpectedError     -> handleUnexpectedErrorResponse(response as InstallDataUnexpectedErrorResponse, datasetID, projectID)
    }
  }

  private fun <T> withDataTar(s3Dir: DatasetDirectory, fn: (Path) -> T): T {
    TempFiles.withTempDirectory { tempDir ->
      val tarFile      = tempDir.resolve("dataset.tar.gz")
      val metaFile     = tempDir.resolve(S3Paths.META_FILE_NAME)
      val manifestFile = tempDir.resolve(S3Paths.MANIFEST_FILE_NAME)
      val files        = mutableListOf(metaFile, manifestFile)

      metaFile.outputStream()
        .use { out -> s3Dir.getMeta().loadContents()!!.use { inp -> inp.transferTo(out) } }

      manifestFile.outputStream()
        .use { out -> s3Dir.getManifest().loadContents()!!.use { inp -> inp.transferTo(out) } }

      s3Dir.getDataFiles()
        .forEach { ddf ->
          val df = tempDir.resolve(ddf.name)
          files.add(df)

          df.outputStream()
            .use { out -> ddf.loadContents()!!.use { inp -> inp.transferTo(out) } }
        }

      Tar.compressWithGZip(tarFile, files)

      return fn(tarFile)
    }
  }

  private fun handleSuccessResponse(res: InstallDataSuccessResponse, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset {} was installed successfully into project {}", datasetID, projectID)

    updateDataSyncControl(datasetID, projectID)

    if (res.warnings.isNotEmpty()) {
      log.debug("dataset {} installed into project {} with warnings, writing warnings to app db", datasetID, projectID)

      AppDB.withTransaction(projectID) {
        it.updateDatasetInstallMessage(DatasetInstallMessage(
          datasetID,
          InstallType.Data,
          InstallStatus.Complete,
          res.warnings.joinToString("\n")
        ))
      }
    }
  }

  private fun handleBadRequestResponse(res: InstallDataBadRequestResponse, datasetID: DatasetID, projectID: ProjectID) {
    log.error("dataset {} install into {} failed due to bad request exception from handler server: {}", datasetID, projectID, res.message)

    updateDataSyncControl(datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw Exception(res.message)
  }

  private fun handleValidationFailureResponse(res: InstallDataValidationFailureResponse, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset {} install into {} failed due to validation error", datasetID, projectID)

    updateDataSyncControl(datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        res.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleMissingDependenciesResponse(res: InstallDataMissingDependenciesResponse, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset {} install into {} was rejected for missing dependencies", datasetID, projectID)

    updateDataSyncControl(datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        res.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleUnexpectedErrorResponse(res: InstallDataUnexpectedErrorResponse, datasetID: DatasetID, projectID: ProjectID) {
    log.error("dataset {} install into {} failed with a 500 from the handler server", datasetID, projectID)

    updateDataSyncControl(datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        res.message
      ))
    }

    throw Exception(res.message)
  }

  private fun updateDataSyncControl(datasetID: DatasetID, projectID: ProjectID) {
    log.trace("updateDataSyncControl(datasetID={}, projectID={})", datasetID, projectID)

    val timestamp = OffsetDateTime.now()

    log.debug("updating cache db data install timestamp for dataset {}", datasetID)
    CacheDB.withTransaction { it.updateDataSyncControl(datasetID, timestamp) }

    log.debug("updating app db for project {} with data install timestamp for dataset {}", projectID, datasetID)
    AppDB.withTransaction(projectID) { it.updateSyncControlDataTimestamp(datasetID, timestamp) }
  }
}