package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import javax.sql.DataSource
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType
import vdi.lib.db.app.sql.dataset.selectDataset
import vdi.lib.db.app.sql.dataset.selectDatasetsByInstallStatus
import vdi.lib.db.app.sql.dataset_install_message.selectDatasetInstallMessage
import vdi.lib.db.app.sql.dataset_install_message.selectDatasetInstallMessages
import vdi.lib.db.app.sql.dataset_project.selectDatasetProjectLinks
import vdi.lib.db.app.sql.dataset_project.testDatasetProjectLinkExists
import vdi.lib.db.app.sql.dataset_visibility.selectDatasetVisibilityRecords
import vdi.lib.db.app.sql.dataset_visibility.testDatasetVisibilityExists
import vdi.lib.db.app.sql.sync_control.selectAllSyncControl
import vdi.lib.db.app.sql.sync_control.selectSyncControl

internal class AppDBAccessorImpl(
  override val project: ProjectID,
  private val schema: String,
  private val dataSource: DataSource,
  override val platform: AppDBPlatform,
) : AppDBAccessor {
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
