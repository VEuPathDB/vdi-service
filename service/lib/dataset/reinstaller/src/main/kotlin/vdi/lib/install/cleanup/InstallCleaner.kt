package vdi.lib.install.cleanup

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.AppDatabaseRegistry
import vdi.lib.db.app.model.DatasetInstallMessage
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.withTransaction
import vdi.lib.db.cache.CacheDB
import vdi.lib.logging.logger

object InstallCleaner {

  private val log = logger

  @Suppress("NOTHING_TO_INLINE")
  private inline fun msgFailedByProject(datasetID: DatasetID, projectID: ProjectID) =
    "failed to clean broken dataset $datasetID installation from project $projectID due to internal error"

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
        maybeCleanDatasetFromTargetDB(datasetID, projectID, cacheRecord.typeName)
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
  private fun maybeCleanDatasetFromTargetDB(datasetID: DatasetID, projectID: ProjectID, dataType: DataType) {
    try {
      val accessor = AppDB().accessor(projectID, dataType)

      if (accessor == null) {
        log.info("Skipping database clean for dataset {} project {} as the target project is not currently enabled.", datasetID, projectID)
        return
      }

      // Lookup the existing install message for the dataset
      val message = accessor.selectDatasetInstallMessage(datasetID, InstallType.Data)

      // If one does not exist, then the dataset was never installed in the
      // first place.
      if (message == null) {
        log.debug("skipping uninstall of dataset {} from project {} as it has no install message", datasetID, projectID)
        return
      }

      // If the status is not failed installation, then we shouldn't touch it.
      if (message.status != InstallStatus.FailedInstallation) {
        log.debug("skipping uninstall of dataset {} from project {} as it is not in a failed state", datasetID, projectID)
        return
      }

      cleanTarget(datasetID, projectID, dataType)
    } catch (e: Throwable) {
      log.error(msgFailedByProject(datasetID, projectID), e)
    }
  }

  private fun cleanTarget(datasetID: DatasetID, projectID: ProjectID, dataType: DataType) {

    log.debug("marking dataset {} as ready-for-reinstall in project {}", datasetID, projectID)

    // Update the dataset install message in the database.
    AppDB().withTransaction(projectID, dataType) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.ReadyForReinstall,
        null
      ))
    }
  }
}

