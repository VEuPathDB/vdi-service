package org.veupathdb.vdi.lib.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.db.app.model.*
import org.veupathdb.vdi.lib.db.app.sql.*
import javax.sql.DataSource

internal class AppDBAccessorImpl(
  private val schema: String,
  private val dataSource: DataSource
) : AppDBAccessor {

  private val log = LoggerFactory.getLogger(javaClass)

  private inline val con
    get() = dataSource.connection

  override fun selectDataset(datasetID: DatasetID): DatasetRecord? {
    log.debug("selecting dataset record for dataset {}", datasetID)
    return con.use { it.selectDataset(schema, datasetID) }
  }

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType): DatasetInstallMessage? {
    log.debug("selecting dataset install message for dataset {} and install type {}", datasetID, installType)
    return con.use { it.selectDatasetInstallMessage(schema, datasetID, installType) }
  }

  override fun selectDatasetInstallMessages(datasetID: DatasetID): List<DatasetInstallMessage> {
    log.debug("selecting dataset install messages for dataset {}", datasetID)
    return con.use { it.selectDatasetInstallMessages(schema, datasetID) }
  }

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID): VDISyncControlRecord? {
    log.debug("selecting dataset sync control record for dataset {}", datasetID)
    return con.use { it.selectSyncControl(schema, datasetID) }
  }

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID): List<DatasetVisibilityRecord> {
    log.debug("selecting dataset visibility records for dataset {}", datasetID)
    return con.use { it.selectDatasetVisibilityRecords(schema, datasetID) }
  }

  override fun selectDatasetProjectLinks(datasetID: DatasetID): List<DatasetProjectLinkRecord> {
    log.debug("selecting dataset project links for dataset {}", datasetID)
    return con.use { it.selectDatasetProjectLinks(schema, datasetID) }
  }

  override fun streamAllSyncControlRecords(): CloseableIterator<VDIReconcilerTargetRecord> {
    log.debug("Streaming all sync control records")
    return con.selectAllSyncControl(schema)
  }

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID): Boolean {
    log.debug("testing dataset visibility for dataset {} and user {}", datasetID, userID)
    return con.use { it.testDatasetVisibilityExists(schema, datasetID, userID) }
  }

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID): Boolean {
    log.debug("testing dataset project link for dataset {} and project {}", datasetID, projectID)
    return con.use { it.testDatasetProjectLinkExists(schema, datasetID, projectID) }
  }

  override fun selectDatasetsByInstallStatus(
    installType: InstallType,
    installStatus: InstallStatus
  ): List<DatasetRecord> {
    log.debug("selecting datasets with install type {} in the status {}", installType, installStatus)
    return con.use { it.selectDatasetsByInstallStatus(schema, installType, installStatus) }
  }
}