package vdi.component.reinstaller

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import vdi.component.db.app.withTransaction
import vdi.component.metrics.Metrics
import vdi.component.plugin.client.PluginHandlerClient
import vdi.component.plugin.client.response.ind.*
import vdi.component.plugin.client.response.uni.UninstallBadRequestResponse
import vdi.component.plugin.client.response.uni.UninstallResponseType
import vdi.component.plugin.client.response.uni.UninstallUnexpectedErrorResponse
import vdi.component.plugin.mapping.PluginHandlers
import vdi.component.s3.DatasetDirectory
import vdi.component.s3.DatasetManager
import vdi.component.s3.paths.S3Paths
import java.io.InputStream
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

object DatasetReinstaller {

  private val log = LoggerFactory.getLogger(javaClass)

  private val lock = ReentrantLock()

  private val config = DatasetReinstallerConfig()

  private var runCounter = 0uL

  private val appDB = AppDB()

  /**
   * Tries to run the [DatasetReinstaller] if an instance of the reinstaller is
   * not already in progress.
   *
   * If a run of the `DatasetReinstaller` is already in progress, this method
   * will return `false` immediately.
   *
   * If a run of the `DatasetReinstaller` is _not_ already in progress, this
   * method will return `true` after the process has completed.
   */
  suspend fun tryRun(): Boolean {
    return if (lock.tryLock()) {
      try {
        run()
        true
      } finally {
        lock.unlock()
      }
    } else {
      false
    }
  }

  private suspend fun run() {
    log.info("starting dataset reinstaller run {}", ++runCounter)

    val s3 = S3Api.newClient(config.s3Config)
    val bucket = s3.buckets[config.s3Bucket]!!
    val manager = DatasetManager(bucket)

    // For each project registered with the service...
    for ((projectID, _) in AppDatabaseRegistry.iterator()) {
      try {
        processProject(projectID, manager)
      } catch (e: Throwable) {
        Metrics.Reinstaller.failedReinstallProcessForProject.labels(projectID).inc()
        log.error("failed to process dataset reinstallations for project $projectID", e)
      }
    }

    log.info("dataset reinstaller run {} complete", runCounter)
  }

  private suspend fun processProject(projectID: ProjectID, manager: DatasetManager) {
    // locate datasets in the ready-for-reinstall status
    val datasets = appDB.accessor(projectID)!!
      .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.ReadyForReinstall)

    log.info("found {} datasets in project {} that are ready for reinstall", datasets.size, projectID)

    // for each located dataset for the target project...
    for (dataset in datasets) {
      try {
        processDataset(dataset, projectID, manager)
      } catch (e: Throwable) {
        Metrics.Reinstaller.failedDatasetReinstall.inc()
        log.error("failed to process dataset ${dataset.owner}/${dataset.datasetID} reinstallation for project $projectID", e)
      }
    }
  }

  private suspend fun processDataset(dataset: DatasetRecord, projectID: ProjectID, manager: DatasetManager) {
    log.info("reinstall processing dataset {}/{} for project {}", dataset.owner, dataset.datasetID, projectID)

    // Get a plugin handler for the target dataset type
    val handler = PluginHandlers[dataset.typeName, dataset.typeVersion]
      ?: throw IllegalStateException("no plugin handler registered for dataset type ${dataset.typeName}")

    // If the handler doesn't apply to the current project then something has
    // gone wrong or the service configuration has changed since this dataset
    // was created.
    if (!handler.appliesToProject(projectID))
      throw IllegalStateException("dataset type ${dataset.typeName} does not apply to project $projectID")

    uninstallDataset(dataset, projectID, handler.client)
    reinstallDataset(dataset, projectID, handler.client, manager)
  }

  private suspend fun uninstallDataset(dataset: DatasetRecord, projectID: ProjectID, client: PluginHandlerClient) {
    log.debug("attempting to uninstall dataset {}/{} from project {}", dataset.owner, dataset.datasetID, projectID)

    val uninstallResult = client.postUninstall(dataset.datasetID, projectID)

    when (uninstallResult.type) {
      UninstallResponseType.Success
      -> { /* do nothing */ }

      UninstallResponseType.BadRequest
      -> throw IllegalStateException("uninstall failed with 400 error: " + (uninstallResult as UninstallBadRequestResponse).message)

      UninstallResponseType.UnexpectedError
      -> throw IllegalStateException("uninstall failed with 500 error: " + (uninstallResult as UninstallUnexpectedErrorResponse).message)
    }
  }

  private suspend fun reinstallDataset(
    dataset: DatasetRecord,
    projectID: ProjectID,
    client: PluginHandlerClient,
    manager: DatasetManager,
  ) {
    log.debug("attempting to reinstall dataset {}/{} into project {}", dataset.owner, dataset.datasetID, projectID)

    val directory = manager.getDatasetDirectory(dataset.owner, dataset.datasetID)

    if (!directory.isReinstallable()) {
      log.warn("skipping reinstall of dataset {}/{} into project {} due to the dataset directory being in an invalid state in the data store", dataset.owner, dataset.datasetID, projectID)
      return
    }

    val response = withInstallBundle(directory) { client.postInstallData(dataset.datasetID, projectID, it) }

    when (response.type) {
      InstallDataResponseType.Success
      -> handleInstallSuccess(response as InstallDataSuccessResponse, dataset, projectID)

      InstallDataResponseType.BadRequest
      -> handleInstallBadRequest(response as InstallDataBadRequestResponse, dataset, projectID)

      InstallDataResponseType.ValidationFailure
      -> handleInstallValidationFailure(response as InstallDataValidationFailureResponse, dataset, projectID)

      InstallDataResponseType.MissingDependencies
      -> handleInstallMissingDependency(response as InstallDataMissingDependenciesResponse, dataset, projectID)

      InstallDataResponseType.UnexpectedError
      -> handleInstallUnexpectedError(response as InstallDataUnexpectedErrorResponse, dataset, projectID)
    }
  }

  private fun DatasetDirectory.isReinstallable(): Boolean {
    var ok = true

    if (!hasMetaFile()) {
      log.warn("dataset {}/{} has no meta file present in the data store", ownerID, datasetID)
      ok = false
    }

    if (!hasManifestFile()) {
      log.warn("dataset {}/{} has no manifest file present in the data store", ownerID, datasetID)
      ok = false
    }

    if (!hasInstallReadyFile()) {
      log.warn("dataset {}/{} has no install-ready file present in the data store", ownerID, datasetID)
      ok = false
    }

    return ok
  }

  private fun handleInstallSuccess(res: InstallDataSuccessResponse, dataset: DatasetRecord, projectID: ProjectID) {
    log.info("dataset {}/{} was reinstalled successfully into project {}", dataset.owner, dataset.datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.warnings.takeUnless { it.isEmpty() }?.joinToString("\n")
      ))
    }
  }

  private fun handleInstallBadRequest(
    response:  InstallDataBadRequestResponse,
    dataset:   DatasetRecord,
    projectID: ProjectID
  ) {
    log.error("dataset {}/{} reinstall into {} failed due to bad request exception from handler server: {}", dataset.owner, dataset.datasetID, projectID, response.message)

    appDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        response.message
      ))
    }

    throw Exception(response.message)
  }

  private fun handleInstallValidationFailure(
    response:  InstallDataValidationFailureResponse,
    dataset:   DatasetRecord,
    projectID: ProjectID
  ) {
    log.info("dataset {}/{} reinstall into {} failed due to validation error", dataset.owner, dataset.datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        response.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallMissingDependency(
    response:  InstallDataMissingDependenciesResponse,
    dataset:   DatasetRecord,
    projectID: ProjectID,
  ) {
    log.info("dataset {}/{} reinstall into {} was rejected for missing dependencies", dataset.owner, dataset.datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        response.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallUnexpectedError(
    response:  InstallDataUnexpectedErrorResponse,
    dataset:   DatasetRecord,
    projectID: ProjectID,
  ) {
    log.error("dataset {}/{} reinstall into {} failed with a 500 from the handler server", dataset.owner, dataset.datasetID, projectID)

    appDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        response.message
      ))
    }

    throw Exception(response.message)
  }

  private suspend fun <T> withInstallBundle(s3Dir: DatasetDirectory, fn: suspend (upload: InputStream) -> T) =
    TempFiles.withTempDirectory { tmpDir ->
      val files = ArrayList<Path>(8)

      TempFiles.withTempFile { zipFile ->
        zipFile.outputStream()
          .buffered()
          .use { out -> s3Dir.getInstallReadyFile().loadContents()!!.buffered().use { inp -> inp.transferTo(out) } }

        Zip.zipEntries(zipFile)
          .forEach { (entry, stream) ->
            tmpDir.resolve(entry.name)
              .also(files::add)
              .outputStream()
              .buffered()
              .use { stream.buffered().transferTo(it) }
          }
      }

      tmpDir.resolve(S3Paths.MetadataFileName)
        .also(files::add)
        .outputStream()
        .buffered()
        .use { out -> s3Dir.getMetaFile().loadContents()!!.use { input -> input.transferTo(out) } }

      tmpDir.resolve(S3Paths.ManifestFileName)
        .also(files::add)
        .outputStream()
        .buffered()
        .use { out -> s3Dir.getManifestFile().loadContents()!!.use { input -> input.transferTo(out) } }

      val zip = tmpDir.resolve("install-bundle.zip")
        .also { Zip.compress(it, files, Zip.Level(0u)) }

      files.forEach { it.deleteIfExists() }
      files.clear()

      zip.inputStream().buffered().use { fn(it) }
    }
}