package vdi.component.db.app

import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import vdi.component.db.app.model.DatasetInstallMessage
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.model.InstallType
import java.time.OffsetDateTime

interface AppDBTransaction : AppDBAccessor, AutoCloseable {

  // region Delete Operations

  /**
   * Deletes the dataset record for a target dataset.
   *
   * **WARNING**: Due to foreign key restraints in the database, this record
   * cannot be deleted before every other linked record is also deleted.
   *
   * @param datasetID ID of the target dataset that should be deleted.
   */
  fun deleteDataset(datasetID: DatasetID)

  fun deleteDatasetContacts(datasetID: DatasetID)

  fun deleteDatasetHyperlinks(datasetID: DatasetID)

  /**
   * Deletes the sync control record for a target dataset.
   *
   * **WARNING**: Deleting this record will leave the dataset in a broken,
   * incomplete state.  This method should only be called as part of a full
   * delete of a dataset.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be deleted.
   */
  fun deleteSyncControl(datasetID: DatasetID)

  /**
   * Deletes the installation messages for a target dataset.
   *
   * **WARNING**: Deleting this record will leave the dataset in a broken,
   * incomplete state.  This method should only be called as part of a full
   * delete of a dataset.
   *
   * @param datasetID ID of the target dataset whose install messages should be
   * deleted.
   */
  fun deleteInstallMessages(datasetID: DatasetID)

  /**
   * Deletes an installation message for a target dataset and install type.
   *
   * @param datasetID ID of the target dataset whose install message should be
   * deleted.
   *
   * @param installType Install type for the install message that should be
   * deleted.
   */
  fun deleteInstallMessage(datasetID: DatasetID, installType: InstallType)

  /**
   * Deletes the dataset visibility records for a target dataset.
   *
   * **WARNING**: Deleting this record will leave the dataset in a broken,
   * incomplete state.  This method should only be called as part of a full
   * delete of a dataset.
   *
   * @param datasetID ID of the target dataset whose visibility records should
   * be deleted.
   */
  fun deleteDatasetVisibilities(datasetID: DatasetID)

  /**
   * Deletes the project link records for a target dataset.
   *
   * **WARNING**: Deleting this record will leave the dataset in a broken,
   * incomplete state.  This method should only be called as part of a full
   * delete of a dataset.
   *
   * @param datasetID ID of the target dataset whose project link records should
   * be deleted.
   */
  fun deleteDatasetProjectLinks(datasetID: DatasetID)

  /**
   * Deletes a specific project link record for a target dataset and project.
   *
   * @param datasetID ID of the dataset whose project link should be deleted.
   *
   * @param projectID ID of the project to which the link should be deleted.
   */
  fun deleteDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID)

  fun deleteDatasetPublications(datasetID: DatasetID)

  fun deleteDatasetTaxonIDs(datasetID: DatasetID)

  /**
   * Deletes a specific visibility record for a target dataset and user.
   *
   * @param datasetID ID of the dataset whose visibility record should be
   * deleted.
   *
   * @param userID ID of the use for which the dataset visibility record should
   * be deleted.
   */
  fun deleteDatasetVisibility(datasetID: DatasetID, userID: UserID)

  /**
   * Deletes a specific dataset meta record.
   *
   * @param datasetID ID of the dataset whose dataset_meta record should be
   * deleted.
   */
  fun deleteDatasetMeta(datasetID: DatasetID)

  // endregion Delete Operations

  // region Insert Operations

  /**
   * Inserts a new dataset record.
   *
   * @param dataset Dataset record to insert.
   */
  fun insertDataset(dataset: DatasetRecord)

  fun insertDatasetContacts(datasetID: DatasetID, contacts: Collection<VDIDatasetContact>)

  fun insertDatasetHyperlinks(datasetID: DatasetID, hyperlinks: Collection<VDIDatasetHyperlink>)

  /**
   * Inserts a new dataset install message record.
   *
   * @param message Dataset install message to insert.
   */
  fun insertDatasetInstallMessage(message: DatasetInstallMessage)

  /**
   * Inserts a new project link for a dataset.
   *
   * @param datasetID ID of the dataset for which a project link should be
   * inserted.
   *
   * @param projectID ID of the project for which a project link should be
   * inserted.
   */
  fun insertDatasetProjectLink(datasetID: DatasetID, projectID: ProjectID)

  /**
   * Inserts new project links for a dataset.
   *
   * @param datasetID ID of the dataset for which project links should be
   * inserted.
   *
   * @param projectIDs IDs of the projects for which project links should be
   * inserted.
   */
  fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<ProjectID>)

  fun insertDatasetPublications(datasetID: DatasetID, publications: Collection<VDIDatasetPublication>)

  /**
   * Inserts a new dataset visibility record for a target dataset and user.
   *
   * Dataset visibility records declare that a dataset should be visible to a
   * target user via the user interface of a VEuPathDB site.
   *
   * @param datasetID ID of the dataset for which a visibility record should be
   * created.
   *
   * @param userID ID of the user for which a visibility record should be
   * created.
   */
  fun insertDatasetVisibility(datasetID: DatasetID, userID: UserID)

  /**
   * Inserts a sync control record with default timestamps for all fields set
   * to [OriginTimestamp].
   *
   * The sync control record, once created, will need to be updated as various
   * sync operations are completed for the target dataset.
   */
  fun insertDatasetSyncControl(sync: VDISyncControlRecord)

  /**
   * Inserts a dataset meta record for the target dataset.
   */
  fun insertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta)

  fun insertDatasetTaxonIDs(datasetID: DatasetID, taxonIDs: Collection<Long>)

  // endregion Insert Operations

  // region Update Operations

  /**
   * Updates a dataset record to the new values set in the given [DatasetRecord]
   * instance.
   *
   * @param dataset Dataset record containing the new values to set on the row
   * in the database.
   */
  fun updateDataset(dataset: DatasetRecord)

  /**
   * Updates the deleted flag for a target dataset record to the given value.
   *
   * @param datasetID ID of the target dataset whose deleted flag should be
   * updated.
   *
   * @param deleteFlag The new value for the deleted flag.
   */
  fun updateDatasetDeletedFlag(datasetID: DatasetID, deleteFlag: DeleteFlag)

  /**
   * Updates the "data" last updated field on a target dataset sync control
   * record.
   *
   * @param datasetID ID of the dataset whose sync control record should be
   * updated.
   *
   * @param timestamp New timestamp to set for the data last updated field.
   */
  fun updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime)

  /**
   * Updates the "meta" last updated field on a target dataset sync control
   * record.
   *
   * @param datasetID ID of the dataset whose sync control record should be
   * updated.
   *
   * @param timestamp New timestamp to set for the meta last updated field.
   */
  fun updateSyncControlMetaTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime)

  /**
   * Updates the "shares" last updated field on a target dataset sync control
   * record.
   *
   * @param datasetID ID of the dataset whose sync control record should be
   * updated.
   *
   * @param timestamp New timestamp to set for the shares last updated field.
   */
  fun updateSyncControlSharesTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime)

  /**
   * Updates the target dataset install message setting the new values provided
   * in the given [DatasetInstallMessage] instance.
   *
   * @param message Message instance containing the new values to set on the
   * target database record.
   */
  fun updateDatasetInstallMessage(message: DatasetInstallMessage)

  /**
   * Updates the target dataset metadata with the new given values.
   */
  fun updateDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta)

  // endregion Update Operations

  // region Upsert Operations

  fun upsertDatasetInstallMessage(message: DatasetInstallMessage)

  /**
   * Upserts the target dataset metadata with the given values.  If the target
   * record already exists it will be updated, if it does not exist, it will be
   * created.
   */
  fun upsertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta)

  // endregion Upsert Operations

  fun rollback()

  fun commit()
}
