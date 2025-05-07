package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime
import vdi.lib.db.app.model.*
import vdi.lib.db.app.sql.dataset.*
import vdi.lib.db.app.sql.dataset_contact.deleteDatasetContacts
import vdi.lib.db.app.sql.dataset_contact.insertDatasetContacts
import vdi.lib.db.app.sql.dataset_dependency.deleteDatasetDependencies
import vdi.lib.db.app.sql.dataset_dependency.insertDatasetDependencies
import vdi.lib.db.app.sql.dataset_hyperlink.deleteDatasetHyperlinks
import vdi.lib.db.app.sql.dataset_hyperlink.insertDatasetHyperlinks
import vdi.lib.db.app.sql.dataset_install_message.*
import vdi.lib.db.app.sql.dataset_meta.deleteDatasetMeta
import vdi.lib.db.app.sql.dataset_meta.insertDatasetMeta
import vdi.lib.db.app.sql.dataset_meta.updateDatasetMeta
import vdi.lib.db.app.sql.dataset_meta.upsertDatasetMeta
import vdi.lib.db.app.sql.dataset_organism.deleteDatasetOrganisms
import vdi.lib.db.app.sql.dataset_organism.insertDatasetOrganisms
import vdi.lib.db.app.sql.dataset_project.*
import vdi.lib.db.app.sql.dataset_properties.deleteDatasetProperties
import vdi.lib.db.app.sql.dataset_properties.insertDatasetProperties
import vdi.lib.db.app.sql.dataset_publication.deleteDatasetPublications
import vdi.lib.db.app.sql.dataset_publication.insertDatasetPublications
import vdi.lib.db.app.sql.dataset_visibility.*
import vdi.lib.db.app.sql.sync_control.*
import vdi.lib.db.model.SyncControlRecord

class AppDBTransactionImpl(
  override val project: ProjectID,
  private val schema: String,
  private val connection: Connection,
  override val platform: AppDBPlatform,
) : AppDBTransaction {
  init {
    connection.autoCommit = false
  }

  // region dataset

  override fun deleteDataset(datasetID: DatasetID) {
    connection.deleteDataset(schema, datasetID)
  }

  override fun insertDataset(dataset: DatasetRecord) {
    connection.insertDataset(schema, dataset)
  }

  override fun selectDataset(datasetID: DatasetID) =
    connection.selectDataset(schema, datasetID)

  override fun selectDatasetsByInstallStatus(installType: InstallType, installStatus: InstallStatus) =
    connection.selectDatasetsByInstallStatus(schema, installType, installStatus, project)

  override fun updateDataset(dataset: DatasetRecord) {
    connection.updateDataset(schema, dataset)
  }

  override fun updateDatasetDeletedFlag(datasetID: DatasetID, deleteFlag: DeleteFlag) {
    connection.updateDatasetDeletedFlag(schema, datasetID, deleteFlag)
  }

  // endregion dataset

  // region dataset_contact

  override fun deleteDatasetContacts(datasetID: DatasetID) {
    connection.deleteDatasetContacts(schema, datasetID)
  }

  override fun insertDatasetContacts(datasetID: DatasetID, contacts: Collection<VDIDatasetContact>) {
    connection.insertDatasetContacts(schema, datasetID, contacts)
  }

  // endregion dataset_contact

  // region dataset_dependency

  override fun deleteDatasetDependencies(datasetID: DatasetID) {
    connection.deleteDatasetDependencies(schema, datasetID)
  }

  override fun insertDatasetDependencies(datasetID: DatasetID, dependencies: Collection<VDIDatasetDependency>) {
    connection.insertDatasetDependencies(schema, datasetID, dependencies)
  }

  // endregion dataset_dependency

  // region dataset_hyperlink

  override fun deleteDatasetHyperlinks(datasetID: DatasetID) {
    connection.deleteDatasetHyperlinks(schema, datasetID)
  }

  override fun insertDatasetHyperlinks(datasetID: DatasetID, hyperlinks: Collection<VDIDatasetHyperlink>) {
    connection.insertDatasetHyperlinks(schema, datasetID, hyperlinks)
  }

  // endregion dataset_hyperlink

  // region dataset_install_message

  override fun deleteInstallMessage(datasetID: DatasetID, installType: InstallType) {
    connection.deleteInstallMessage(schema, datasetID, installType)
  }

  override fun deleteInstallMessages(datasetID: DatasetID) {
    connection.deleteInstallMessages(schema, datasetID)
  }

  override fun insertDatasetInstallMessage(message: DatasetInstallMessage) {
    connection.insertDatasetInstallMessage(schema, message)
  }

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType) =
    connection.selectDatasetInstallMessage(schema, datasetID, installType)

  override fun selectDatasetInstallMessages(datasetID: DatasetID) =
    connection.selectDatasetInstallMessages(schema, datasetID)

  override fun updateDatasetInstallMessage(message: DatasetInstallMessage) {
    connection.updateDatasetInstallMessage(schema, message)
  }

  override fun upsertDatasetInstallMessage(message: DatasetInstallMessage) {
    if (platform === AppDBPlatform.Postgres) {
      connection.upsertDatasetInstallMessage(schema, message)
    } else {
      try {
        insertDatasetInstallMessage(message)
      } catch (e: SQLException) {
        if (isUniqueConstraintViolation(e))
          updateDatasetInstallMessage(message)
        else
          throw e
      }
    }
  }

  // endregion dataset_install_message

  // region dataset_meta

  override fun deleteDatasetMeta(datasetID: DatasetID) {
    connection.deleteDatasetMeta(schema, datasetID)
  }

  override fun insertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) {
    connection.insertDatasetMeta(schema, datasetID, meta)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) {
    connection.updateDatasetMeta(schema, datasetID, meta)
  }

  override fun upsertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) {
    if (platform === AppDBPlatform.Postgres) {
      connection.upsertDatasetMeta(schema, datasetID, meta)
    } else {
      try {
        insertDatasetMeta(datasetID, meta)
      } catch (e: SQLException) {
        if (isUniqueConstraintViolation(e)) {
          updateDatasetMeta(datasetID, meta)
        } else {
          throw e
        }
      }
    }
  }

  // endregion dataset_meta

  // region dataset_organism

  override fun deleteDatasetOrganisms(datasetID: DatasetID) {
    connection.deleteDatasetOrganisms(schema, datasetID)
  }

  override fun insertDatasetOrganisms(datasetID: DatasetID, organisms: Collection<String>) {
    connection.insertDatasetOrganisms(schema, datasetID, organisms)
  }

  // endregion dataset_organism

  // region dataset_project

  override fun deleteDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    connection.deleteDatasetProjectLink(schema, datasetID, projectID)
  }

  override fun deleteDatasetProjectLinks(datasetID: DatasetID) {
    connection.deleteDatasetProjectLinks(schema, datasetID)
  }

  override fun insertDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID) {
    connection.insertDatasetProjectLink(schema, datasetID, projectID)
  }

  override fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<ProjectID>) {
    connection.insertDatasetProjectLinks(schema, datasetID, projectIDs)
  }

  override fun selectDatasetProjectLinks(datasetID: DatasetID) =
    connection.selectDatasetProjectLinks(schema, datasetID)

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID) =
    connection.testDatasetProjectLinkExists(schema, datasetID, projectID)

  // endregion dataset_project

  // region dataset_properties

  override fun deleteDatasetProperties(datasetID: DatasetID) {
    connection.deleteDatasetProperties(schema, datasetID)
  }

  override fun insertDatasetProperties(datasetID: DatasetID, properties: VDIDatasetProperties) {
    connection.insertDatasetProperties(schema, datasetID, properties)
  }

  // endregion dataset_properties

  // region dataset_publication

  override fun deleteDatasetPublications(datasetID: DatasetID) {
    connection.deleteDatasetPublications(schema, datasetID)
  }

  override fun insertDatasetPublications(datasetID: DatasetID, publications: Collection<VDIDatasetPublication>) {
    connection.insertDatasetPublications(schema, datasetID, publications)
  }

  // endregion dataset_publication

  // region dataset_visibility

  override fun deleteDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    connection.deleteDatasetVisibility(schema, datasetID, userID)
  }

  override fun deleteDatasetVisibilities(datasetID: DatasetID) {
    connection.deleteDatasetVisibilities(schema, datasetID)
  }

  override fun insertDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    connection.insertDatasetVisibility(schema, datasetID, userID)
  }

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID) =
    connection.selectDatasetVisibilityRecords(schema, datasetID)

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID) =
    connection.testDatasetVisibilityExists(schema, datasetID, userID)

  // endregion dataset_visibility

  // region sync_control

  override fun deleteSyncControl(datasetID: DatasetID) {
    connection.deleteSyncControl(schema, datasetID)
  }

  override fun insertDatasetSyncControl(sync: SyncControlRecord) {
    connection.insertDatasetSyncControl(schema, sync)
  }

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID) =
    connection.selectSyncControl(schema, datasetID)

  override fun streamAllSyncControlRecords() =
    connection.selectAllSyncControl(schema)

  override fun updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlDataTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlMetaTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlMetaTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlSharesTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlSharesTimestamp(schema, datasetID, timestamp)
  }

  // endregion sync_control

  override fun rollback() {
    connection.rollback()
  }

  override fun commit() {
    connection.commit()
  }

  override fun close() {
    connection.close()
  }
}
