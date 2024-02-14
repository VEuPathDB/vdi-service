package org.veupathdb.vdi.lib.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.cache.model.*
import java.time.OffsetDateTime

interface CacheDBTransaction : AutoCloseable {

  fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID)

  fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID)

  /**
   * Deletes the `vdi.dataset_metadata` table entry for a target dataset
   * identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.dataset_metadata`
   * record should be deleted.
   */
  fun deleteDatasetMetadata(datasetID: DatasetID)

  /**
   * Deletes all entries in the `vdi.dataset_projects` table for a target
   * dataset identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.dataset_projects`
   * records should be deleted.
   */
  fun deleteDatasetProjects(datasetID: DatasetID)

  /**
   * Deletes all entries in the `vdi.dataset_share_offers` table for a target
   * dataset identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.dataset_share_offers`
   * records should be deleted.
   */
  fun deleteDatasetShareOffers(datasetID: DatasetID)

  /**
   * Deletes all entries in the `vdi.dataset_share_receipts` table for a target
   * dataset identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose
   * `vdi.dataset_share_receipts` records should be deleted.
   */
  fun deleteDatasetShareReceipts(datasetID: DatasetID)

  /**
   * Deletes the `vdi.datasets` table entry for a target dataset identified by
   * the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.datasets` record
   * should be deleted.
   */
  fun deleteDataset(datasetID: DatasetID)

  /**
   * Deletes the `vdi.import_control` table entry for a target dataset
   * identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.import_control` record
   * should be deleted.
   */
  fun deleteImportControl(datasetID: DatasetID)

  /**
   * Deletes the `vdi.import_messages` table entry for a target dataset
   * identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.import_messages`
   * record should be deleted.
   */
  fun deleteImportMessages(datasetID: DatasetID)

  /**
   * Deletes the `vdi.sync_control` table entry for a target dataset identified
   * by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.sync_control` record
   * should be deleted.
   */
  fun deleteSyncControl(datasetID: DatasetID)

  fun deleteInstallFiles(datasetID: DatasetID)

  fun deleteUploadFiles(datasetID: DatasetID)


  fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>)


  /**
   * Attempts to insert a dataset record for the given dataset details, aborting
   * the insert query silently if a conflicting record already exists.
   *
   * @param row Row containing the dataset details to insert into the database.
   */
  fun tryInsertDataset(row: Dataset)

  fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>)

  /**
   * Attempts to insert dataset metadata for a target dataset, aborting the
   * insert query silently if a record already exists.
   *
   * @param row Dataset metadata to insert.
   */
  fun tryInsertDatasetMeta(row: DatasetMeta)

  /**
   * Attempts to insert dataset-to-project links for a target dataset, aborting
   * any conflicting inserts silently if they exist.
   *
   * @param datasetID ID of the target dataset for which project link records
   * should be inserted.
   *
   * @param projects Collection of project links to insert for the target
   * dataset.
   */
  fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Collection<ProjectID>)

  /**
   * Attempts to insert an import control record for the target dataset,
   * aborting the query silently if a conflicting record already exists.
   *
   * @param datasetID ID of the target dataset for which an import control
   * record should be inserted.
   *
   * @param status Import status to set.
   */
  fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus)

  /**
   * Attempts to insert a sync control record for the target dataset, aborting
   * the query silently if a conflicting record already exists.
   *
   * @param record VDI sync control record to insert.
   */
  fun tryInsertSyncControl(record: VDISyncControlRecord)

  fun tryInsertImportMessages(datasetID: DatasetID, messages: String)

  // endregion Try-Insert

  fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus)

  fun updateDatasetMeta(row: DatasetMeta)

  /**
   * Updates the sync control record for a target dataset setting the meta
   * update time to the given new value only if the new value is more recent
   * than the value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the meta field in the target sync
   * control record.
   */
  fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime)

  /**
   * Updates the sync control record for a target dataset setting the data
   * update time to the given new value only if the new value is more recent
   * than the value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the data field in the target sync
   * control record.
   */
  fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime)

  /**
   * Updates the sync control record for a target dataset setting the share
   * update time to the given new value only if the new value is more recent
   * than the value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the share field in the target sync
   * control record.
   */
  fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime)

  fun upsertDatasetShareOffer(row: DatasetShareOffer)

  fun upsertDatasetShareReceipt(row: DatasetShareReceipt)

  fun upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus)

  fun upsertImportMessages(datasetID: DatasetID, messages: String)

  fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean)

  fun commit()

  fun rollback()
}
