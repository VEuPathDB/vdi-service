package vdi.core.db.app

import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime
import vdi.core.db.app.model.*
import vdi.core.db.app.sql.dataset.*
import vdi.core.db.app.sql.dataset_contact.deleteDatasetContacts
import vdi.core.db.app.sql.dataset_contact.insertDatasetContacts
import vdi.core.db.app.sql.dataset_dependency.deleteDatasetDependencies
import vdi.core.db.app.sql.dataset_dependency.insertDatasetDependencies
import vdi.core.db.app.sql.dataset_hyperlink.deleteDatasetHyperlinks
import vdi.core.db.app.sql.dataset_hyperlink.insertDatasetHyperlinks
import vdi.core.db.app.sql.dataset_install_message.*
import vdi.core.db.app.sql.dataset_meta.deleteDatasetMeta
import vdi.core.db.app.sql.dataset_meta.insertDatasetMeta
import vdi.core.db.app.sql.dataset_meta.updateDatasetMeta
import vdi.core.db.app.sql.dataset_meta.upsertDatasetMeta
import vdi.core.db.app.sql.dataset_organism.deleteDatasetOrganisms
import vdi.core.db.app.sql.dataset_organism.insertDatasetOrganisms
import vdi.core.db.app.sql.dataset_project.*
import vdi.core.db.app.sql.dataset_properties.deleteDatasetProperties
import vdi.core.db.app.sql.dataset_properties.insertDatasetProperties
import vdi.core.db.app.sql.dataset_publication.deleteDatasetPublications
import vdi.core.db.app.sql.dataset_publication.insertDatasetPublications
import vdi.core.db.app.sql.dataset_visibility.*
import vdi.core.db.app.sql.sync_control.*
import vdi.core.db.model.SyncControlRecord
import vdi.db.app.TargetDBPlatform
import vdi.model.data.*

class AppDBTransactionImpl(
  override val project: InstallTargetID,
  private val schema: String,
  private val connection: Connection,
  override val platform: TargetDBPlatform,
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

  override fun insertDatasetContacts(datasetID: DatasetID, contacts: Collection<DatasetContact>) {
    connection.insertDatasetContacts(schema, datasetID, contacts)
  }

  // endregion dataset_contact

  // region dataset_dependency

  override fun deleteDatasetDependencies(datasetID: DatasetID) {
    connection.deleteDatasetDependencies(schema, datasetID)
  }

  override fun insertDatasetDependencies(datasetID: DatasetID, dependencies: Collection<DatasetDependency>) {
    connection.insertDatasetDependencies(schema, datasetID, dependencies)
  }

  // endregion dataset_dependency

  // region dataset_hyperlink

  override fun deleteDatasetHyperlinks(datasetID: DatasetID) {
    connection.deleteDatasetHyperlinks(schema, datasetID)
  }

  override fun insertDatasetHyperlinks(datasetID: DatasetID, hyperlinks: Collection<DatasetHyperlink>) {
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
    if (platform === TargetDBPlatform.Postgres) {
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

  override fun insertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    connection.insertDatasetMeta(schema, datasetID, meta)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    connection.updateDatasetMeta(schema, datasetID, meta)
  }

  override fun upsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    if (platform === TargetDBPlatform.Postgres) {
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

  override fun deleteDatasetProjectLink(datasetID: DatasetID, installTarget: InstallTargetID) {
    connection.deleteDatasetProjectLink(schema, datasetID, installTarget)
  }

  override fun deleteDatasetProjectLinks(datasetID: DatasetID) {
    connection.deleteDatasetProjectLinks(schema, datasetID)
  }

  override fun insertDatasetProjectLink(datasetID: DatasetID, installTarget: InstallTargetID) {
    connection.insertDatasetProjectLink(schema, datasetID, installTarget)
  }

  override fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<InstallTargetID>) {
    connection.insertDatasetProjectLinks(schema, datasetID, projectIDs)
  }

  override fun selectDatasetProjectLinks(datasetID: DatasetID) =
    connection.selectDatasetProjectLinks(schema, datasetID)

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, installTarget: InstallTargetID) =
    connection.testDatasetProjectLinkExists(schema, datasetID, installTarget)

  // endregion dataset_project

  // region dataset_properties

  override fun deleteDatasetProperties(datasetID: DatasetID) {
    connection.deleteDatasetProperties(schema, datasetID)
  }

  override fun insertDatasetProperties(datasetID: DatasetID, properties: DatasetProperties) {
    connection.insertDatasetProperties(schema, datasetID, properties)
  }

  // endregion dataset_properties

  // region dataset_publication

  override fun deleteDatasetPublications(datasetID: DatasetID) {
    connection.deleteDatasetPublications(schema, datasetID)
  }

  override fun insertDatasetPublications(datasetID: DatasetID, publications: Collection<DatasetPublication>) {
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
    connection.selectAllSyncControl(schema, platform)

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
