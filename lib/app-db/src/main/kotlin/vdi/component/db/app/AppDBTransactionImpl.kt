package vdi.component.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.app.model.*
import vdi.component.db.app.sql.*
import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime

class AppDBTransactionImpl(
  override val project: ProjectID,
  private val schema: String,
  private val connection: Connection,
  private val platform: AppDBPlatform,
) : AppDBTransaction {

  private val log = LoggerFactory.getLogger(javaClass)

  init {
    connection.autoCommit = false
  }

  override fun deleteDataset(datasetID: DatasetID) {
    log.debug("deleting dataset record for dataset {}", datasetID)
    connection.deleteDataset(schema, datasetID)
  }

  override fun deleteSyncControl(datasetID: DatasetID) {
    log.debug("deleting sync control record for dataset {}", datasetID)
    connection.deleteSyncControl(schema, datasetID)
  }

  override fun deleteInstallMessage(datasetID: DatasetID, installType: InstallType) {
    log.debug("deleting install message for dataset {} and install type {}", datasetID, installType)
    connection.deleteInstallMessage(schema, datasetID, installType)
  }

  override fun deleteInstallMessages(datasetID: DatasetID) {
    log.debug("deleting all install messages for dataset {}", datasetID)
    connection.deleteInstallMessages(schema, datasetID)
  }

  override fun deleteDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    log.debug("deleting dataset visibility record for dataset {} and user {}", datasetID, userID)
    connection.deleteDatasetVisibility(schema, datasetID, userID)
  }

  override fun deleteDatasetVisibilities(datasetID: DatasetID) {
    log.debug("deleting all dataset visibility records for dataset {}", datasetID)
    connection.deleteDatasetVisibilities(schema, datasetID)
  }

  override fun deleteDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    log.debug("deleting dataset project link for dataset {} and project {}", datasetID, projectID)
    connection.deleteDatasetProjectLink(schema, datasetID, projectID)
  }

  override fun deleteDatasetProjectLinks(datasetID: DatasetID) {
    log.debug("deleting all dataset project links for dataset {}", datasetID)
    connection.deleteDatasetProjectLinks(schema, datasetID)
  }

  override fun deleteDatasetMeta(datasetID: DatasetID) {
    log.debug("deleting dataset meta for dataset {}", datasetID)
    connection.deleteDatasetMeta(schema, datasetID)
  }

  override fun insertDataset(dataset: DatasetRecord) {
    log.debug("inserting dataset record for dataset {}", dataset.datasetID)
    connection.insertDataset(schema, dataset)
  }

  override fun insertDatasetInstallMessage(message: DatasetInstallMessage) {
    log.debug("inserting dataset install message for dataset {}, install type {}", message.datasetID, message.installType)
    connection.insertDatasetInstallMessage(schema, message)
  }

  override fun upsertDatasetInstallMessage(message: DatasetInstallMessage) {
    log.debug("upserting dataset install message for dataset {}, install type {}", message.datasetID, message.installType)
    if (platform === AppDBPlatform.Postgres) {
      connection.upsertDatasetInstallMessage(schema, message)
    } else {
      try {
        insertDatasetInstallMessage(message)
      } catch (e: SQLException) {
        if (e.errorCode == UniqueConstraintViolation)
          updateDatasetInstallMessage(message)
        else
          throw e
      }
    }
  }

  override fun insertDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    log.debug("inserting dataset project link for dataset {}, project {}", datasetID, projectID)
    connection.insertDatasetProjectLink(schema, datasetID, projectID)
  }

  override fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<ProjectID>) {
    log.debug("inserting dataset project links for dataset {}", datasetID)
    connection.insertDatasetProjectLinks(schema, datasetID, projectIDs)
  }

  override fun insertDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    log.debug("inserting dataset visibility record for dataset {}, user {}", datasetID, userID)
    connection.insertDatasetVisibility(schema, datasetID, userID)
  }

  override fun insertSyncControl(sync: VDISyncControlRecord) {
    log.debug("inserting dataset sync control record for dataset {}", sync.datasetID)
    connection.insertDatasetSyncControl(schema, sync)
  }

  override fun insertDatasetMeta(datasetID: DatasetID, name: String, description: String?) {
    log.debug("inserting dataset meta record for dataset {}", datasetID)
    connection.insertDatasetMeta(schema, datasetID, name, description)
  }

  override fun upsertDatasetMeta(datasetID: DatasetID, name: String, description: String?) {
    log.debug("upserting dataset meta record for dataset {}", datasetID)
    if (platform === AppDBPlatform.Postgres) {
      connection.upsertDatasetMeta(schema, datasetID, name, description)
    } else {
      try {
        insertDatasetMeta(datasetID, name, description)
      } catch (e: SQLException) {
        if (e.errorCode == 1) {
          log.debug("dataset meta record already exists for dataset {}", datasetID)
          updateDatasetMeta(datasetID, name, description)
        } else {
          throw e
        }
      }
    }
  }

  override fun updateDataset(dataset: DatasetRecord) {
    log.debug("updating dataset record for dataset {}", dataset.datasetID)
    connection.updateDataset(schema, dataset)
  }

  override fun updateDatasetDeletedFlag(datasetID: DatasetID, deleteFlag: DeleteFlag) {
    log.debug("updating dataset record deleted flag for dataset {} to {}", datasetID, deleteFlag)
    connection.updateDatasetDeletedFlag(schema, datasetID, deleteFlag)
  }

  override fun updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control data timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlDataTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlMetaTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control meta timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlMetaTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlSharesTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control shares timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlSharesTimestamp(schema, datasetID, timestamp)
  }

  override fun updateDatasetInstallMessage(message: DatasetInstallMessage) {
    log.debug("updating dataset install message for dataset {}, install type {}", message.datasetID, message.installType)
    connection.updateDatasetInstallMessage(schema, message)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, name: String, description: String?) {
    log.debug("updating dataset meta record for dataset {}", datasetID)
    connection.updateDatasetMeta(schema, datasetID, name, description)
  }

  override fun rollback() {
    connection.rollback()
  }

  override fun commit() {
    connection.commit()
  }

  override fun close() {
    connection.close()
  }

  override fun selectDataset(datasetID: DatasetID) = connection.selectDataset(schema, datasetID)

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType) =
    connection.selectDatasetInstallMessage(schema, datasetID, installType)

  override fun selectDatasetInstallMessages(datasetID: DatasetID) =
    connection.selectDatasetInstallMessages(schema, datasetID)

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID) =
    connection.selectSyncControl(schema, datasetID)

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID) =
    connection.selectDatasetVisibilityRecords(schema, datasetID)

  override fun selectDatasetProjectLinks(datasetID: DatasetID) =
    connection.selectDatasetProjectLinks(schema, datasetID)

  override fun streamAllSyncControlRecords() = connection.selectAllSyncControl(schema)

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID) =
    connection.testDatasetVisibilityExists(schema, datasetID, userID)

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID) =
    connection.testDatasetProjectLinkExists(schema, datasetID, projectID)

  override fun selectDatasetsByInstallStatus(installType: InstallType, installStatus: InstallStatus) =
    connection.selectDatasetsByInstallStatus(schema, installType, installStatus, project)
}