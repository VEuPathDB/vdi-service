package vdi.core.db.cache

import java.time.OffsetDateTime
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.model.SyncControlRecord
import vdi.model.DatasetUploadStatus
import vdi.model.meta.*

interface CacheDBTransaction: CacheDB, AutoCloseable {

  // region Delete

  /**
   * Deletes a singular share offer record for a target dataset/recipient user
   * combination.
   *
   * @param datasetID ID of the target dataset from which the share offer record
   * should be removed.
   *
   * @param recipientID ID of the share offer recipient user.
   *
   * @return A flag indicating whether a record was actually deleted as a result
   * of this method call.
   */
  fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID): Boolean

  fun deleteShareOffers(datasetID: DatasetID): Int

  /**
   * Deletes a singular share receipt record for a target dataset/recipient user
   * combination.
   *
   * @param datasetID ID of the target dataset from which the share receipt
   * record should be removed.
   *
   * @param recipientID ID of the share receipt recipient user.
   *
   * @return A flag indicating whether a record was actually deleted as a result
   * of this method call.
   */
  fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID): Boolean

  fun deleteShareReceipts(datasetID: DatasetID): Int

  /**
   * Deletes the `vdi.import_messages` table entry for a target dataset
   * identified by the given [DatasetID].
   *
   * @param datasetID ID of the target dataset whose `vdi.import_messages`
   * record should be deleted.
   *
   * @return The number of records that were deleted as a result of this method
   * call.
   */
  fun deleteImportMessages(datasetID: DatasetID): Int

  /**
   * Deletes the entire revision history record for a dataset by using the
   * original dataset's ID.
   *
   * @param originalID ID of the original dataset in the dataset revision
   * history.
   *
   * @return The number of records that were deleted as a result of this method
   * call.
   */
  fun deleteRevisions(originalID: DatasetID): Int

  fun deletePublication(datasetID: DatasetID, publicationID: String): Boolean

  /**
   * Deletes all publication records associated with a target dataset.
   *
   * @param datasetID ID of the target dataset.
   *
   * @return The number of records that were deleted as a result of this method
   * call.
   */
  fun deletePublications(datasetID: DatasetID): Int

  fun deleteInstallFiles(datasetID: DatasetID): Int

  fun deleteUploadFiles(datasetID: DatasetID): Int

  fun deleteImportControlRecords(datasetID: DatasetID): Int

  fun deleteSyncControlRecords(datasetID: DatasetID): Int

  fun deleteDatasetMetadata(datasetID: DatasetID): Boolean

  fun deleteDatasetInstallTargets(datasetID: DatasetID): Int

  // endregion Delete

  // region Try Insert

  /**
   * Attempts to insert user-upload file metadata records for a target dataset,
   * doing nothing for records that already exist.
   *
   * @param datasetID ID of the dataset for which the records should be
   * inserted.
   *
   * @param files User upload file information records.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>): Int

  /**
   * Attempts to insert a dataset record for the given dataset details, aborting
   * the insert query silently if a conflicting record already exists.
   *
   * @param row Row containing the dataset details to insert into the database.
   *
   * @return A flag indicating whether a record was actually inserted as a
   * result of this method call.
   */
  fun tryInsertDataset(row: Dataset): Boolean

  /**
   * Attempts to insert installable file metadata records for a target dataset,
   * doing nothing for records that already exist.
   *
   * @param datasetID ID of the dataset for which the records should be
   * inserted.
   *
   * @param files Installable file information records.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>): Int

  /**
   * Attempts to insert dataset metadata for a target dataset, aborting the
   * insert query silently if a record already exists.
   *
   * @return A flag indicating whether a record was actually inserted as a
   * result of this method call.
   */
  fun tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata): Boolean

  /**
   * Attempts to insert dataset-to-project links for a target dataset, aborting
   * any conflicting inserts silently if they exist.
   *
   * @param datasetID ID of the target dataset for which project link records
   * should be inserted.
   *
   * @param projects Collection of project links to insert for the target
   * dataset.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<InstallTargetID>): Int

  /**
   * Attempts to insert an import control record for the target dataset,
   * aborting the query silently if a conflicting record already exists.
   *
   * @param datasetID ID of the target dataset for which an import control
   * record should be inserted.
   *
   * @param status Import status to set.
   *
   * @return A flag indicating whether a record was actually inserted as a
   * result of this method call.
   */
  fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus): Boolean

  /**
   * Attempts to insert a sync control record for the target dataset, aborting
   * the query silently if a conflicting record already exists.
   *
   * @param record VDI sync control record to insert.
   *
   * @return A flag indicating whether a record was actually inserted as a
   * result of this method call.
   */
  fun tryInsertSyncControl(record: SyncControlRecord): Boolean

  /**
   * Attempts to insert import messages for a target dataset, doing nothing on
   * record conflicts.
   *
   * @param datasetID ID of the dataset for which import messages should be
   * inserted.
   *
   * @param messages Import messages to insert.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertImportMessages(datasetID: DatasetID, messages: Iterable<String>): Int

  /**
   * Attempts to insert a dataset revision history link record from a new
   * dataset revision to the original dataset ID, doing nothing if such a record
   * already exists.
   *
   * @param originalID ID of the first dataset upload in the revision history.
   *
   * @param revision New dataset revision details
   *
   * @return A flag indicating whether a record was actually inserted as a
   * result of this method call.
   */
  fun tryInsertRevisionLink(originalID: DatasetID, revision: DatasetRevision): Boolean

  /**
   * Attempts to insert dataset revision history link records from dataset
   * revisions to the original dataset ID, skipping any conflicting records.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertRevisionLinks(history: DatasetRevisionHistory): Int

  /**
   * Attempts to insert dataset publications records for a target dataset,
   * skipping any conflicting rows.
   *
   * @param datasetID ID of the first dataset upload in the revision history.
   *
   * @param publications Publication records to insert.
   *
   * @return The number of records that were actually inserted as a result of
   * this method call.
   */
  fun tryInsertPublications(datasetID: DatasetID, publications: Iterable<DatasetPublication>): Int

  // endregion Try-Insert

  // region Update

  fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus): Boolean

  fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata): Boolean

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
   *
   * @return A flag indicating whether a record was actually updated as a result
   * of this method call.
   */
  fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime): Boolean

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
   *
   * @return A flag indicating whether a record was actually updated as a result
   * of this method call.
   */
  fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime): Boolean

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
   *
   * @return A flag indicating whether a record was actually updated as a result
   * of this method call.
   */
  fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime): Boolean

  /**
   * Updates the soft delete marker flag for the target dataset to the given
   * value.
   *
   * @return A flag indicating whether a record was actually updated as a result
   * of this method call.
   */
  fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean): Boolean

  // endregion Update

  // region Upsert

  fun upsertDatasetShareOffer(row: ShareOfferRecord)

  fun upsertDatasetShareReceipt(row: ShareReceiptRecord)

  fun upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus)

  fun upsertUploadStatus(datasetID: DatasetID, status: DatasetUploadStatus)

  // endregion Upsert

  fun commit()

  fun rollback()
}
