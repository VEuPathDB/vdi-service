package vdi.component.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import vdi.component.db.app.sql.select.*
import vdi.component.db.app.sql.select.selectAllSyncControl
import vdi.component.db.app.sql.select.selectDataset
import vdi.component.db.app.sql.select.selectDatasetsByInstallStatus
import vdi.component.db.app.sql.select.selectSyncControl
import javax.sql.DataSource

internal class AppDBAccessorImpl(
  override val project: ProjectID,
  private val schema: String,
  private val dataSource: DataSource
) : AppDBAccessor {

  private val log = LoggerFactory.getLogger(javaClass)

  private inline val con
    get() = dataSource.connection

  override fun selectDataset(datasetID: DatasetID) = con.use { it.selectDataset(schema, datasetID) }

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType) =
    con.use { it.selectDatasetInstallMessage(schema, datasetID, installType) }

  override fun selectDatasetInstallMessages(datasetID: DatasetID) =
    con.use { it.selectDatasetInstallMessages(schema, datasetID) }

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID) =
    con.use { it.selectSyncControl(schema, datasetID) }

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID) =
    con.use { it.selectDatasetVisibilityRecords(schema, datasetID) }

  override fun selectDatasetProjectLinks(datasetID: DatasetID) =
    con.use { it.selectDatasetProjectLinks(schema, datasetID) }

  override fun streamAllSyncControlRecords() = con.selectAllSyncControl(schema)

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID) =
    con.use { it.testDatasetVisibilityExists(schema, datasetID, userID) }

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID) =
    con.use { it.testDatasetProjectLinkExists(schema, datasetID, projectID) }

  override fun selectDatasetsByInstallStatus(installType: InstallType, installStatus: InstallStatus) =
    con.use { it.selectDatasetsByInstallStatus(schema, installType, installStatus, project) }
}
