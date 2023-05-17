package org.veupathdb.vdi.lib.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.app.model.*
import org.veupathdb.vdi.lib.db.app.sql.*
import java.sql.Connection
import java.time.OffsetDateTime

class AppDBTransactionImpl(private val connection: Connection) : AppDBTransaction {

  private val log = LoggerFactory.getLogger(javaClass)

  init {
    connection.autoCommit = false
  }

  override fun deleteDataset(datasetID: DatasetID) {
    log.debug("deleting dataset record for dataset {}", datasetID)
    connection.deleteDataset(datasetID)
  }

  override fun deleteSyncControl(datasetID: DatasetID) {
    log.debug("deleting sync control record for dataset {}", datasetID)
    connection.deleteSyncControl(datasetID)
  }

  override fun deleteInstallMessage(datasetID: DatasetID, installType: InstallType) {
    log.debug("deleting install message for dataset {} and install type {}", datasetID, installType)
    connection.deleteInstallMessage(datasetID, installType)
  }

  override fun deleteInstallMessages(datasetID: DatasetID) {
    log.debug("deleting all install messages for dataset {}", datasetID)
    connection.deleteInstallMessages(datasetID)
  }

  override fun deleteDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    log.debug("deleting dataset visibility record for dataset {} and user {}", datasetID, userID)
    connection.deleteDatasetVisibility(datasetID, userID)
  }

  override fun deleteDatasetVisibilities(datasetID: DatasetID) {
    log.debug("deleting all dataset visibility records for dataset {}", datasetID)
    connection.deleteDatasetVisibilities(datasetID)
  }

  override fun deleteDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    log.debug("deleting dataset project link for dataset {} and project {}", datasetID, projectID)
    connection.deleteDatasetProjectLink(datasetID, projectID)
  }

  override fun deleteDatasetProjectLinks(datasetID: DatasetID) {
    log.debug("deleting all dataset project links for dataset {}", datasetID)
    connection.deleteDatasetProjectLinks(datasetID)
  }


  override fun insertDataset(dataset: DatasetRecord) {
    log.debug("inserting dataset record for dataset {}", dataset.datasetID)
    connection.insertDataset(dataset)
  }

  override fun insertDatasetInstallMessage(message: DatasetInstallMessage) {
    log.debug("inserting dataset install message for dataset {}, install type {}", message.datasetID, message.installType)
    connection.insertDatasetInstallMessage(message)
  }

  override fun insertDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    log.debug("inserting dataset project link for dataset {}, project {}", datasetID, projectID)
    connection.insertDatasetProjectLink(datasetID, projectID)
  }

  override fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<ProjectID>) {
    log.debug("inserting dataset project links for dataset {}", datasetID)
    connection.insertDatasetProjectLinks(datasetID, projectIDs)
  }

  override fun insertDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    log.debug("inserting dataset visibility record for dataset {}, user {}", datasetID, userID)
    connection.insertDatasetVisibility(datasetID, userID)
  }

  override fun insertSyncControl(sync: VDISyncControlRecord) {
    log.debug("inserting dataset sync control record for dataset {}", sync.datasetID)
    connection.insertDatasetSyncControl(sync)
  }


  override fun updateDataset(dataset: DatasetRecord) {
    log.debug("updating dataset record for dataset {}", dataset.datasetID)
    connection.updateDataset(dataset)
  }

  override fun updateDatasetDeletedFlag(datasetID: DatasetID, deleted: Boolean) {
    log.debug("updating dataset record deleted flag for dataset {} to {}", datasetID, deleted)
    connection.updateDatasetDeletedFlag(datasetID, deleted)
  }

  override fun updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control data timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlDataTimestamp(datasetID, timestamp)
  }

  override fun updateSyncControlMetaTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control meta timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlMetaTimestamp(datasetID, timestamp)
  }

  override fun updateSyncControlSharesTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating dataset sync control shares timestamp for dataset {} to {}", datasetID, timestamp)
    connection.updateSyncControlSharesTimestamp(datasetID, timestamp)
  }

  override fun updateDatasetInstallMessage(message: DatasetInstallMessage) {
    log.debug("updating dataset install message for dataset {}, install type {}", message.datasetID, message.installType)
    connection.updateDatasetInstallMessage(message)
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

  override fun selectDataset(datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset record for dataset {}", datasetID)
    return connection.selectDataset(datasetID)
  }

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType): DatasetInstallMessage? {
    log.debug("selecting dataset install message for dataset {} and install type {}", datasetID, installType)
    return connection.selectDatasetInstallMessage(datasetID, installType)
  }

  override fun selectDatasetInstallMessages(datasetID: DatasetID): List<DatasetInstallMessage> {
    log.debug("selecting dataset install messages for dataset {}", datasetID)
    return connection.selectDatasetInstallMessages(datasetID)
  }

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID): VDISyncControlRecord? {
    log.debug("selecting dataset sync control record for dataset {}", datasetID)
    return connection.selectSyncControl(datasetID)
  }

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID): List<DatasetVisibilityRecord> {
    log.debug("selecting dataset visibility records for dataset {}", datasetID)
    return connection.selectDatasetVisibilityRecords(datasetID)
  }

  override fun selectDatasetProjectLinks(datasetID: DatasetID): List<DatasetProjectLinkRecord> {
    log.debug("selecting dataset project links for dataset {}", datasetID)
    return connection.selectDatasetProjectLinks(datasetID)
  }

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID): Boolean {
    log.debug("testing dataset visibility for dataset {} and user {}", datasetID, userID)
    return connection.testDatasetVisibilityExists(datasetID, userID)
  }

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID): Boolean {
    log.debug("testing dataset project link for dataset {} and project {}", datasetID, projectID)
    return connection.testDatasetProjectLinkExists(datasetID, projectID)
  }
}