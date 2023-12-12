package vdi.component.reinstaller

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.compression.Tar
import org.veupathdb.vdi.lib.common.compression.Zip
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.fs.TempFiles
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.response.ind.*
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallBadRequestResponse
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallResponseType
import org.veupathdb.vdi.lib.handler.client.response.uni.UninstallUnexpectedErrorResponse
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

object DatasetReinstaller {

  private val log = LoggerFactory.getLogger(javaClass)

  private val lock = ReentrantLock()

  private val config = DatasetReinstallerConfig()

  private var runCounter = 0uL

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
  fun tryRun(): Boolean {
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

  private fun run() {
    log.info("starting dataset reinstaller run {}", ++runCounter)

    // For each project registered with the service...
    for ((projectID, _) in AppDatabaseRegistry.iterator()) {
      try {
        processProject(projectID)
      } catch (e: Throwable) {
        log.error("failed to process dataset reinstallations for project $projectID", e)
      }
    }

    log.info("dataset reinstaller run {} complete", runCounter)
  }

  private fun processProject(projectID: ProjectID) {
    // locate datasets in the ready-for-reinstall status
    val datasets = AppDB.accessor(projectID)
      .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.ReadyForReinstall)

    log.info("found {} datasets in project {} that are ready for reinstall", datasets.size, projectID)

    // for each located dataset for the target project...
    for (dataset in datasets) {
      try {
        processDataset(dataset, projectID)
      } catch (e: Throwable) {
        log.error("failed to process dataset ${dataset.datasetID} reinstallation for project $projectID", e)
      }
    }
  }

  private fun processDataset(dataset: DatasetRecord, projectID: ProjectID) {
    log.info("reinstall processing dataset {} for project {}", dataset.datasetID, projectID)

    // Get a plugin handler for the target dataset type
    val handler = PluginHandlers.get(dataset.typeName, dataset.typeVersion)
      ?: throw IllegalStateException("no plugin handler registered for dataset type ${dataset.typeName}")

    // If the handler doesn't apply to the current project then something has
    // gone wrong or the service configuration has changed since this dataset
    // was created.
    if (!handler.appliesToProject(projectID))
      throw IllegalStateException("dataset type ${dataset.typeName} does not apply to project $projectID")

    uninstallDataset(dataset, projectID, handler.client)
    reinstallDataset(dataset, projectID, handler.client)
  }

  private fun uninstallDataset(dataset: DatasetRecord, projectID: ProjectID, client: PluginHandlerClient) {

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

  private fun reinstallDataset(dataset: DatasetRecord, projectID: ProjectID, client: PluginHandlerClient) {
    val s3        = S3Api.newClient(config.s3Config)
    val bucket    = s3.buckets[config.s3Bucket]!!
    val manager   = DatasetManager(bucket)
    val directory = manager.getDatasetDirectory(dataset.owner, dataset.datasetID)

    val response =  withDataTar(directory) { dataTar ->
      dataTar.inputStream()
        .use { inp -> client.postInstallData(dataset.datasetID, projectID, inp) }
    }

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
  private fun <T> withDataTar(s3Dir: DatasetDirectory, fn: (Path) -> T): T {
    TempFiles.withTempDirectory { tempDir ->
      val tarFile      = tempDir.resolve("dataset.tar.gz")
      val metaFile     = tempDir.resolve(DatasetMetaFilename)
      val manifestFile = tempDir.resolve(DatasetManifestFilename)
      val files        = mutableListOf(metaFile, manifestFile)

      metaFile.outputStream()
        .use { out -> s3Dir.getMeta().loadContents()!!.use { inp -> inp.transferTo(out) } }

      manifestFile.outputStream()
        .use { out -> s3Dir.getManifest().loadContents()!!.use { inp -> inp.transferTo(out) } }

      s3Dir.getDataFiles()
        .forEach { ddf ->
          val zip = tempDir.resolve(ddf.name)
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

  private fun handleInstallSuccess(res: InstallDataSuccessResponse, dataset: DatasetRecord, projectID: ProjectID) {
    log.info("dataset {} was reinstalled successfully into project {}", dataset.datasetID, projectID)

    AppDB.withTransaction(projectID) {
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
    log.error("dataset {} reinstall into {} failed due to bad request exception from handler server: {}", dataset.datasetID, projectID, response.message)

    AppDB.withTransaction(projectID) {
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
    log.info("dataset {} reinstall into {} failed due to validation error", dataset.datasetID, projectID)

    AppDB.withTransaction(projectID) {
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
    log.info("dataset {} reinstall into {} was rejected for missing dependencies", dataset.datasetID, projectID)

    AppDB.withTransaction(projectID) {
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
    log.error("dataset {} reinstall into {} failed with a 500 from the handler server", dataset.datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        response.message
      ))
    }

    throw Exception(response.message)
  }
}