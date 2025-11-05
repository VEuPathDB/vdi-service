package vdi.core.install.retry

import kotlinx.coroutines.sync.Mutex
import org.slf4j.Logger
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.errors.S34KError
import vdi.model.data.InstallTargetID
import java.io.InputStream
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.withTransaction
import vdi.logging.logger
import vdi.core.metrics.Metrics
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
import vdi.core.s3.DatasetObjectStore
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.logging.mark
import vdi.model.EventID
import vdi.util.events.EventIDs

object DatasetReinstaller {

  private val log = logger()

  private val lock = Mutex()

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
    val manager = DatasetObjectStore(bucket)

    // For each project registered with the service...
    for ((projectID, _) in AppDatabaseRegistry) {
      try {
        processProject(projectID, manager)
      } catch (e: Throwable) {
        Metrics.Reinstaller.failedReinstallProcessForProject.labels(projectID).inc()
        log.error("failed to process dataset reinstallations for project $projectID", e)
      }
    }

    log.info("dataset reinstaller run {} complete", runCounter)
  }

  private suspend fun processProject(installTarget: InstallTargetID, manager: DatasetObjectStore) {
    for ((dataType, _) in AppDatabaseRegistry[installTarget]!!) {
      // locate datasets in the ready-for-reinstall status
      val datasets = appDB.accessor(installTarget, dataType)!!
        .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.ReadyForReinstall)

      log.info("found {} datasets in project {} that are ready for reinstall", datasets.size, installTarget)

      // for each located dataset for the target project...
      for (dataset in datasets) {
        try {
          processDataset(dataset, installTarget, manager)
        } catch (e: PluginException) {
          Metrics.Reinstaller.failedDatasetReinstall.inc()
          log.error(
            "failed to process dataset ${dataset.owner}/${dataset.datasetID} reinstallation for project $installTarget via plugin ${e.plugin}",
            e
          )
        } catch (e: Throwable) {
          Metrics.Reinstaller.failedDatasetReinstall.inc()
          log.error(
            "failed to process dataset ${dataset.owner}/${dataset.datasetID} reinstallation for project $installTarget",
            e
          )
        }
      }
    }
  }

  private suspend fun processDataset(dataset: DatasetRecord, installTarget: InstallTargetID, manager: DatasetObjectStore) {
    val logger = log.mark(dataset.owner, dataset.datasetID)
    logger.info("reinstall processing dataset for project {}", installTarget)

    // Get a plugin handler for the target dataset type
    val handler = PluginHandlers[dataset.type]
      ?: throw IllegalStateException("no plugin handler registered for dataset type ${dataset.type}")

    // If the handler doesn't apply to the current project then something has
    // gone wrong or the service configuration has changed since this dataset
    // was created.
    if (!handler.appliesToProject(installTarget))
      throw IllegalStateException("dataset type ${dataset.type} does not apply to project $installTarget")

    val eventID = EventIDs.issueID()

    uninstallDataset(eventID, dataset, installTarget, handler, logger)
    reinstallDataset(eventID, dataset, installTarget, handler, manager, logger)
  }

  private suspend fun uninstallDataset(
    eventID: EventID,
    dataset: DatasetRecord,
    installTarget: InstallTargetID,
    handler: PluginHandler,
    logger: Logger,
  ) {
    logger.debug("attempting to uninstall dataset project {}", installTarget)

    val uninstallResult = try {
      handler.client.postUninstall(eventID, dataset.datasetID, installTarget, dataset.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    }

    when (uninstallResult.status) {
      PluginResponseStatus.Success -> { /* do nothing */ }

      PluginResponseStatus.ScriptError -> throw PluginException.uninstall(
        handler.name,
        installTarget,
        dataset.owner,
        dataset.datasetID,
        (uninstallResult as ScriptErrorResponse).body.lastMessage,
      )

      PluginResponseStatus.ServerError -> throw PluginException.uninstall(
        handler.name,
        installTarget,
        dataset.owner,
        dataset.datasetID,
        (uninstallResult as ServerErrorResponse).body.message,
      )

      else -> {
        if (uninstallResult is StreamResponse)
          uninstallResult.body.close()

        throw IllegalStateException("unrecognized uninstall result status: ${uninstallResult.status}")
      }
    }
  }

  private suspend fun reinstallDataset(
    eventID:       EventID,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    handler:       PluginHandler,
    manager:       DatasetObjectStore,
    logger:        Logger,
  ) {
    logger.debug("attempting to reinstall dataset into project {}", installTarget)

    val directory = manager.getDatasetDirectory(dataset.owner, dataset.datasetID)

    if (!directory.isReinstallable(logger)) {
      logger.warn("skipping reinstall of dataset into project {} due to the dataset directory being in an invalid state in the data store", installTarget)
      return
    }

    val response = try {
      withInstallBundle(directory) { meta, manifest, data ->
        handler.client.postInstallData(eventID, dataset.datasetID, installTarget, meta, manifest, data)
      }
    } catch (e: S34KError) { // don't mix up minio errors with request errors
      throw PluginException.installData(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    } catch (e: Throwable) {
      throw PluginRequestException.installData(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    }

    when (response.status) {
      PluginResponseStatus.Success ->
        handleInstallSuccess(handler, response as ValidationResponse, dataset, installTarget, logger)

      PluginResponseStatus.BasicValidationError ->
        handleInstallBasicValidationFailure(handler, response as ValidationResponse, dataset, installTarget, logger)

      PluginResponseStatus.CommunityValidationError ->
        handleInstallCommunityValidationFailure(handler, response as ValidationResponse, dataset, installTarget, logger)

      PluginResponseStatus.MissingDependencyError ->
        handleInstallMissingDependency(handler, response as MissingDependencyResponse, dataset, installTarget, logger)

      PluginResponseStatus.ScriptError ->
        handleInstallScriptError(handler, response as ScriptErrorResponse, dataset, installTarget, logger)

      PluginResponseStatus.ServerError ->
        handleInstallServerError(handler, response as ServerErrorResponse, dataset, installTarget, logger)

      else -> {
        if (response is StreamResponse)
          response.body.close()

        throw IllegalStateException("unrecognized uninstall result status: ${response.status}")
      }
    }
  }

  private fun DatasetDirectory.isReinstallable(logger: Logger): Boolean {
    var ok = true

    if (!hasMetaFile()) {
      logger.warn("no meta file present in the data store")
      ok = false
    }

    if (!hasManifestFile()) {
      logger.warn("no manifest file present in the data store")
      ok = false
    }

    if (!hasInstallReadyFile()) {
      logger.warn("no install-ready file present in the data store")
      ok = false
    }

    return ok
  }

  private fun handleInstallSuccess(
    handler: PluginHandler,
    res: ValidationResponse,
    dataset: DatasetRecord,
    installTarget: InstallTargetID,
    logger: Logger,
  ) {
    logger.info("dataset was reinstalled successfully into project {} via plugin {}", installTarget, handler.name)

    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        res.getWarningsSequence().joinToString("\n").takeUnless { it.isEmpty() }
      ))
    }
  }

  private fun handleInstallBasicValidationFailure(
    handler:       PluginHandler,
    response:      ValidationResponse,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ) {
    logger.info(
      "dataset reinstall into {} failed due to validation error in plugin {}",
      installTarget,
      handler.name,
    )

    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedValidation,
        response.getWarningsSequence().joinToString("\n")
      ))
    }
  }

  private fun handleInstallCommunityValidationFailure(
    handler:       PluginHandler,
    response:      ValidationResponse,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ) {
    val status = if (dataset.isPublic)
      InstallStatus.FailedValidation
    else
      InstallStatus.Complete

    logger.info(
      "dataset reinstall into {} failed due to validation error in plugin {}",
      installTarget,
      handler.name,
    )

    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        status,
        response.body.communityValidation.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallMissingDependency(
    handler:       PluginHandler,
    response:      MissingDependencyResponse,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ) {
    logger.info("reinstall into {} was rejected for missing dependencies in plugin {}", installTarget, handler.name)

    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.MissingDependency,
        response.body.warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallScriptError(
    handler: PluginHandler,
    response:  ScriptErrorResponse,
    dataset:   DatasetRecord,
    installTarget: InstallTargetID,
    logger: Logger,
  ): Nothing {
    logger.error(
      "reinstall into {} failed with due to script error in plugin {}",
      installTarget,
      handler.name,
    )

    handleInstall5xx(handler, response.body.lastMessage, dataset, installTarget)
  }

  private fun handleInstallServerError(
    handler:       PluginHandler,
    response:      ServerErrorResponse,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ): Nothing {
    logger.error(
      "reinstall into {} failed with due to plugin {} server error",
      installTarget,
      handler.name,
    )

    handleInstall5xx(handler, response.body.message, dataset, installTarget)
  }

  private fun handleInstall5xx(
    handler:       PluginHandler,
    message:       String,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
  ): Nothing {
    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.FailedInstallation,
        message,
      ))
    }

    throw PluginException.installData(handler.name, installTarget, dataset.owner, dataset.datasetID, message)
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
}
