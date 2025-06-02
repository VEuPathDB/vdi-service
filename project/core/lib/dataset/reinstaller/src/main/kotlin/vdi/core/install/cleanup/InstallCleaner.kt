package vdi.core.install.cleanup

import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.withTransaction
import vdi.core.db.cache.CacheDB
import vdi.logging.logger
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

object InstallCleaner {

  private val log = logger()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun msgFailedByProject(datasetID: DatasetID, installTarget: InstallTargetID) =
    "failed to clean broken dataset $datasetID installation from project $installTarget due to internal error"

  /**
   * Locate all datasets in all application databases that are in an
   * install-failed state and mark them as ready-for-reinstall.
   */
  fun cleanAll() {
    log.info("starting broken install cleanup for all broken datasets")

    // Iterate through all the registered application databases.
    for ((projectID, dataType, _) in AppDatabaseRegistry) {

      try {
        // Fetch a list of datasets with broken installs
        val targets = AppDB().accessor(projectID, dataType)!!
          .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.FailedInstallation)

        log.info("found {} broken datasets for cleanup in project {}", targets.size, projectID)

        // Iterate through the broken datasets and try to clean them
        for (target in targets) {
          try {
            cleanTarget(target.datasetID, projectID, dataType)
          } catch (e: Throwable) {
            log.error(msgFailedByProject(target.datasetID, projectID), e)
          }
        }
      } catch (e: Throwable) {
        log.error("failed to clean datasets for project $projectID:", e)
      }
    }

    log.info("ending broken install cleanup for all broken datasets")
  }

  /**
   * For the given target dataset IDs find any broken installations in any of
   * each dataset's target application databases then update the dataset's
   * status to ready-for-reinstall.
   */
  fun cleanTargets(targets: Iterable<ReinstallTarget>) {
    log.info("starting broken install cleanup for target datasets")

    for ((datasetID, projectID) in targets) {
      val cacheRecord = CacheDB().selectDataset(datasetID)

      if (cacheRecord == null) {
        log.warn("no record for dataset {} found in cache DB; skipping", datasetID)
        continue
      }

      try {
        maybeCleanDatasetFromTargetDB(datasetID, projectID, cacheRecord.type)
      } catch (e: Throwable) {
        log.error("failed to clean broken dataset $datasetID for project $projectID installations due to internal error:", e)
      }
    }

    log.info("ending broken install cleanup for target datasets")
  }

  /**
   * Marks the target dataset as [InstallStatus.ReadyForReinstall] in the target
   * database only if it is already in the [InstallStatus.FailedInstallation]
   * status.
   */
  private fun maybeCleanDatasetFromTargetDB(datasetID: DatasetID, installTarget: InstallTargetID, dataType: DatasetType) {
    try {
      val accessor = AppDB().accessor(installTarget, dataType)

      if (accessor == null) {
        log.info("Skipping database clean for dataset {} project {} as the target project is not currently enabled.", datasetID, installTarget)
        return
      }

      // Lookup the existing install message for the dataset
      val message = accessor.selectDatasetInstallMessage(datasetID, InstallType.Data)

      // If one does not exist, then the dataset was never installed in the
      // first place.
      if (message == null) {
        log.debug("skipping uninstall of dataset {} from project {} as it has no install message", datasetID, installTarget)
        return
      }

      // If the status is not failed installation, then we shouldn't touch it.
      if (message.status != InstallStatus.FailedInstallation) {
        log.debug("skipping uninstall of dataset {} from project {} as it is not in a failed state", datasetID, installTarget)
        return
      }

      cleanTarget(datasetID, installTarget, dataType)
    } catch (e: Throwable) {
      log.error(msgFailedByProject(datasetID, installTarget), e)
    }
  }

  private fun cleanTarget(datasetID: DatasetID, installTarget: InstallTargetID, dataType: DatasetType) {

    log.debug("marking dataset {} as ready-for-reinstall in project {}", datasetID, installTarget)

    // Update the dataset install message in the database.
    AppDB().withTransaction(installTarget, dataType) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.ReadyForReinstall,
        null
      ))
    }
  }
}

