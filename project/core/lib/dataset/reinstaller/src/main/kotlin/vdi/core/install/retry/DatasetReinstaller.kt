package vdi.core.install.retry

import kotlinx.coroutines.sync.Mutex
import org.slf4j.Logger
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.errors.S34KError
import java.io.InputStream
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.withTransaction
import vdi.core.metrics.Metrics
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.*
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.logging.logger
import vdi.model.meta.InstallTargetID
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
        ReinstallerContext(EventIDs.issueID(), dataset, installTarget, manager, log).tryProcessDataset()
      }
    }
  }

  private suspend fun ReinstallerContext.tryProcessDataset() {
    try {
      processDataset()
    } catch (e: Throwable) {
      Metrics.Reinstaller.failedDatasetReinstall.inc()

      if (e is PluginException)
        logger.error("failed to process dataset reinstallation via plugin ${e.plugin}", e)
      else
        logger.error("failed to process dataset reinstallation", e)
    }
  }

  private suspend fun ReinstallerContext.processDataset() {
    logger.info("reinstall processing dataset for project {}", installTarget)

    // Get a plugin handler for the target dataset type
    val handler = PluginHandlers[dataset.type]
      ?: throw IllegalStateException("no plugin handler registered for dataset type ${dataset.type}")

    // If the handler doesn't apply to the current project then something has
    // gone wrong or the service configuration has changed since this dataset
    // was created.
    if (!handler.appliesToProject(installTarget))
      throw IllegalStateException("dataset type ${dataset.type} does not apply to project $installTarget")

    uninstallDataset(handler)
    reinstallDataset(handler)
  }

  private suspend fun ReinstallerContext.uninstallDataset(handler: PluginHandler) {
    logger.debug("attempting to uninstall dataset project {}", installTarget)

    try {
      handler.client.postUninstall(eventID, dataset.datasetID, installTarget, dataset.type)
    } catch (e: Throwable) {
      throw PluginRequestException.uninstall(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    }.use { uninstallResult ->
      when (uninstallResult) {
        is EmptySuccessResponse -> { /* OK */ }

        is ScriptErrorResponse -> throw PluginException.uninstall(
          handler.name,
          installTarget,
          dataset.owner,
          dataset.datasetID,
          uninstallResult.message,
        )

        is ServerErrorResponse -> throw PluginException.uninstall(
          handler.name,
          installTarget,
          dataset.owner,
          dataset.datasetID,
          uninstallResult.message,
        )
      }
    }
  }

  private suspend fun ReinstallerContext.reinstallDataset(handler: PluginHandler) {
    logger.debug("attempting to reinstall dataset into project {}", installTarget)

    val directory = manager.getDatasetDirectory(dataset.owner, dataset.datasetID)

    if (!directory.isReinstallable(logger)) {
      logger.warn("skipping reinstall of dataset into project {} due to the dataset directory being in an invalid state in the data store", installTarget)
      return
    }

    try {
      withInstallBundle(directory) { meta, manifest, data ->
        handler.client.postInstallData(eventID, dataset.datasetID, installTarget, meta, manifest, data)
      }
    } catch (e: S34KError) { // don't mix up minio errors with request errors
      throw PluginException.installData(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    } catch (e: Throwable) {
      throw PluginRequestException.installData(handler.name, installTarget, dataset.owner, dataset.datasetID, cause = e)
    }.use { response ->
      when (response) {
        is SuccessWithWarningsResponse ->
          handleInstallSuccess(handler, response.getWarningsSequence(), dataset, installTarget, logger)

        is ValidationErrorResponse ->
          handleInstallValidationFailure(handler, response.getWarningsSequence(), dataset, installTarget, logger)

        is MissingDependencyResponse ->
          handleInstallMissingDependency(handler, response.body.warnings.asSequence(), dataset, installTarget, logger)

        is ScriptErrorResponse ->
          handleInstallScriptError(handler, response.message, dataset, installTarget, logger)

        is ServerErrorResponse ->
          handleInstallServerError(handler, response.message, dataset, installTarget, logger)
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
    handler:       PluginHandler,
    warnings:      Sequence<String>,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ) {
    logger.info("dataset was reinstalled successfully into project {} via plugin {}", installTarget, handler.name)

    appDB.withTransaction(installTarget, dataset.type) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        dataset.datasetID,
        InstallType.Data,
        InstallStatus.Complete,
        warnings.joinToString("\n").takeUnless(String::isEmpty)
      ))
    }
  }

  private fun handleInstallValidationFailure(
    handler:       PluginHandler,
    warnings:      Sequence<String>,
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
        warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallMissingDependency(
    handler:       PluginHandler,
    warnings:      Sequence<String>,
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
        warnings.joinToString("\n")
      ))
    }
  }

  private fun handleInstallScriptError(
    handler:       PluginHandler,
    message:       String,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ): Nothing {
    logger.error(
      "reinstall into {} failed with due to script error in plugin {}",
      installTarget,
      handler.name,
    )

    handleInstall5xx(handler, message, dataset, installTarget)
  }

  private fun handleInstallServerError(
    handler:       PluginHandler,
    message:       String,
    dataset:       DatasetRecord,
    installTarget: InstallTargetID,
    logger:        Logger,
  ): Nothing {
    logger.error(
      "reinstall into {} failed with due to plugin {} server error",
      installTarget,
      handler.name,
    )

    handleInstall5xx(handler, message, dataset, installTarget)
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
    fn:    suspend (meta: InputStream, manifest: InputStream, upload: InputStream) -> T
  ) =
    s3Dir.getMetaFile().loadContents()!!.use { meta ->
      s3Dir.getManifestFile().loadContents()!!.use { manifest ->
        s3Dir.getInstallReadyFile().loadContents()!!.use { data ->
          fn(meta, manifest, data)
        }
      }
    }
}
