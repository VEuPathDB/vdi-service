package vdi.core.db.app

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import javax.sql.DataSource
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.dataset.selectDataset
import vdi.core.db.app.sql.dataset.selectDatasetsByInstallStatus
import vdi.core.db.app.sql.dataset_install_message.selectDatasetInstallMessage
import vdi.core.db.app.sql.dataset_install_message.selectDatasetInstallMessages
import vdi.core.db.app.sql.dataset_project.selectDatasetProjectLinks
import vdi.core.db.app.sql.dataset_project.testDatasetProjectLinkExists
import vdi.core.db.app.sql.dataset_visibility.selectDatasetVisibilityRecords
import vdi.core.db.app.sql.dataset_visibility.testDatasetVisibilityExists
import vdi.core.db.app.sql.sync_control.selectAllSyncControl
import vdi.core.db.app.sql.sync_control.selectSyncControl
import vdi.lib.db.app.TargetDBPlatform

internal class AppDBAccessorImpl(
  override val project: InstallTargetID,
  private val schema: String,
  private val dataSource: DataSource,
  override val platform: TargetDBPlatform,
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

  override fun streamAllSyncControlRecords() = con.selectAllSyncControl(schema, platform)

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID) =
    con.use { it.testDatasetVisibilityExists(schema, datasetID, userID) }

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, installTarget: InstallTargetID) =
    con.use { it.testDatasetProjectLinkExists(schema, datasetID, installTarget) }

  override fun selectDatasetsByInstallStatus(installType: InstallType, installStatus: InstallStatus) =
    con.use { it.selectDatasetsByInstallStatus(schema, installType, installStatus, project) }
}
