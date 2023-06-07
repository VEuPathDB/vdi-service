package vdi.component.install_cleanup

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import org.veupathdb.vdi.lib.db.cache.CacheDB

object InstallCleaner {

  private val log = LoggerFactory.getLogger(javaClass)

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
    for ((projectID, _) in AppDatabaseRegistry) {
      try {
        // Fetch a list of datasets with broken installs
        val targets = AppDB.accessor(projectID)
          .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.FailedInstallation)

        log.info("found {} broken datasets for cleanup in project {}", targets.size, projectID)

        // Iterate through the broken datasets and try to clean them
        for (target in targets) {
          try {
            cleanTarget(target.datasetID, projectID)
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
  fun cleanTargets(targets: Iterable<DatasetID>) {
    log.info("starting broken install cleanup for target datasets")

    for (target in targets) {
      try {
        cleanTarget(target)
      } catch (e: Throwable) {
        log.error("failed to clean broken dataset $target installations due to internal error:", e)
      }
    }

    log.info("ending broken install cleanup for target datasets")
  }

  private fun cleanTarget(datasetID: DatasetID) {
    val cacheDBRecord = CacheDB.selectDataset(datasetID)
      ?: throw IllegalStateException("target dataset $datasetID is not in the internal cache database")

    for (project in cacheDBRecord.projects) {
      try {
        // Lookup the existing install message for the dataset
        val message = AppDB.accessor(project)
          .selectDatasetInstallMessage(datasetID, InstallType.Data)

        // If one does not exist, then the dataset was never installed in the
        // first place.
        if (message == null) {
          log.debug("skipping uninstall of dataset {} from project {} as it has no install message", datasetID, project)
          continue
        }

        // If the status is not failed installation, then we shouldn't touch it.
        if (message.status != InstallStatus.FailedInstallation) {
          log.debug("skipping uninstall of dataset {} from project {} as it is not in a failed state", datasetID, project)
          continue
        }

        cleanTarget(datasetID, project)
      } catch (e: Throwable) {
        log.error(msgFailedByProject(datasetID, project), e)
      }
    }
  }

  private fun cleanTarget(datasetID: DatasetID, projectID: ProjectID) {

    log.debug("marking dataset {} as ready-for-reinstall in project {}", datasetID, projectID)

    // Update the dataset install message in the database.
    AppDB.withTransaction(projectID) {
      it.updateDatasetInstallMessage(DatasetInstallMessage(
        datasetID,
        InstallType.Data,
        InstallStatus.ReadyForReinstall,
        null
      ))
    }
  }
}

