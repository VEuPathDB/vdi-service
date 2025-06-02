package vdi.core.db.cache

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import vdi.model.data.DatasetFileInfo
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetRevision
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.sql.delete.*
import vdi.core.db.cache.sql.insert.*
import vdi.core.db.cache.sql.update.*
import vdi.core.db.cache.sql.upsert.upsertDatasetShareOffer
import vdi.core.db.cache.sql.upsert.upsertDatasetShareReceipt
import vdi.core.db.cache.sql.upsert.upsertImportControl
import vdi.core.db.cache.sql.upsert.upsertImportMessages
import vdi.core.db.model.SyncControlRecord
import vdi.logging.logger

internal class CacheDBTransactionImpl(private val connection: Connection) : CacheDBTransaction {

  private val log = logger<CacheDBTransaction>()

  private var committed = false

  private var closed = false

  private val con
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      connection

  // region Delete

  override fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID) {
    if (con.deleteShareOffer(datasetID, recipientID))
      log.debug("deleted share offer for dataset {}, recipient {}", datasetID, recipientID)
  }

  override fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) {
    if (con.deleteShareReceipt(datasetID, recipientID))
      log.debug("deleted share receipt for dataset {}, recipient {}", datasetID, recipientID)
  }

  override fun deleteDatasetMetadata(datasetID: DatasetID) {
    if (con.deleteDatasetMetadata(datasetID))
      log.debug("deleted metadata for dataset {}", datasetID)
  }

  override fun deleteDatasetProjects(datasetID: DatasetID) {
    if (con.deleteDatasetProjects(datasetID) > 0)
      log.debug("deleted project links for dataset {}", datasetID)
  }

  override fun deleteDatasetShareOffers(datasetID: DatasetID) {
    val count = con.deleteDatasetShareOffers(datasetID)
    if (count > 0)
      log.debug("deleted {} share offers for dataset {}", count, datasetID)
  }

  override fun deleteDatasetShareReceipts(datasetID: DatasetID) {
    val count = con.deleteDatasetShareReceipts(datasetID)
    if (count > 0)
      log.debug("deleted {} share receipts for dataset {}", count, datasetID)
  }

  override fun deleteDataset(datasetID: DatasetID) {
    if (con.deleteDataset(datasetID))
      log.info("deleted dataset record for {}", datasetID)
  }

  override fun deleteImportControl(datasetID: DatasetID) {
    if (con.deleteImportControl(datasetID) > 0)
      log.debug("deleted import control record for dataset {}", datasetID)
  }

  override fun deleteImportMessages(datasetID: DatasetID) {
    if (con.deleteImportMessages(datasetID) > 0)
      log.debug("deleted import messages for dataset {}", datasetID)
  }

  override fun deleteSyncControl(datasetID: DatasetID) {
    if (con.deleteSyncControl(datasetID) > 0)
      log.debug("deleted sync control record for dataset {}", datasetID)
  }

  override fun deleteInstallFiles(datasetID: DatasetID) {
    if (con.deleteInstallFiles(datasetID) > 0)
      log.debug("deleted install files for dataset {}", datasetID)
  }

  override fun deleteUploadFiles(datasetID: DatasetID) {
    if (con.deleteUploadFiles(datasetID) > 0)
      log.debug("deleting upload files for dataset {}", datasetID)
  }

  override fun deleteRevisions(originalID: DatasetID) {
    val count = con.deleteDatasetRevisions(originalID)
    if (count > 0)
      log.debug("deleted {} revisions for dataset {}", count, originalID)
  }

  // endregion Delete

  // region Try-Insert

  override fun tryInsertDataset(row: Dataset) {
    if (con.tryInsertDatasetRecord(row) > 0)
      log.debug("inserted dataset record for dataset {}", row.datasetID)
  }

  override fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) {
    if (con.tryInsertInstallFiles(datasetID, files) > 0)
      log.debug("inserted dataset install files for dataset {}", datasetID)
  }

  override fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) {
    if (con.tryInsertUploadFiles(datasetID, files) > 0)
      log.debug("inserted dataset upload files for dataset {}", datasetID)
  }

  override fun tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    if (con.tryInsertDatasetMeta(datasetID, meta) > 0)
      log.debug("inserted metadata for dataset {}", datasetID)
  }

  override fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Collection<InstallTargetID>) {
    if (con.tryInsertDatasetProjects(datasetID, projects) > 0)
      log.debug("inserted project links for dataset {}", datasetID)
  }

  override fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    if (con.tryInsertImportControl(datasetID, status) > 0)
      log.debug("inserted import control record for dataset {}", datasetID)
  }

  override fun tryInsertSyncControl(record: SyncControlRecord) {
    if (con.tryInsertSyncControl(record) > 0)
      log.debug("inserted sync control record for dataset {}", record.datasetID)
  }

  override fun tryInsertImportMessages(datasetID: DatasetID, messages: String) {
    if (con.tryInsertImportMessages(datasetID, messages) > 0)
      log.debug("inserted import messages for dataset {}", datasetID)
  }

  override fun tryInsertRevisionLink(originalID: DatasetID, revision: DatasetRevision) {
    if (con.tryInsertDatasetRevision(originalID, revision) > 0)
      log.debug("inserted revision link from dataset {} to revision {}", originalID, revision.revisionID)
  }

  override fun tryInsertRevisionLinks(originalID: DatasetID, revisions: Iterable<DatasetRevision>) {
    val count = con.tryInsertDatasetRevisions(originalID, revisions)
    if (count > 0)
      log.debug("inserted {} revision links for dataset {}", count, originalID)
  }

  // endregion Try-Insert

  override fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("updating import status for dataset {} to status {}", datasetID, status)
    con.updateDatasetImportStatus(datasetID, status)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    log.debug("updating metadata for dataset {}", datasetID)
    con.updateDatasetMeta(datasetID, meta)
  }

  override fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record meta timestamp for dataset {}", datasetID)
    con.updateSyncControlMeta(datasetID, timestamp)
  }

  override fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record data timestamp for dataset {}", datasetID)
    con.updateSyncControlData(datasetID, timestamp)
  }

  override fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record share timestamp for dataset {}", datasetID)
    con.updateSyncControlShare(datasetID, timestamp)
  }

  override fun upsertDatasetShareOffer(row: ShareOfferRecord) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareOffer(row.datasetID, row.recipientID, row.action)
  }

  override fun upsertDatasetShareReceipt(row: ShareReceiptRecord) {
    log.debug("upserting share receipt action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareReceipt(row.datasetID, row.recipientID, row.action)
  }

  override fun upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("upserting import control record for dataset {} for status {}", datasetID, status)
    con.upsertImportControl(datasetID, status)
  }

  override fun upsertImportMessages(datasetID: DatasetID, messages: String) {
    log.debug("upserting import messages for dataset {}", datasetID)
    con.upsertImportMessages(datasetID, messages)
  }

  override fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean) {
    log.debug("updating dataset deleted flag for dataset {} to {}", datasetID, deleted)
    con.updateDatasetDeleteFlag(datasetID, deleted)
  }

  override fun commit() {
    if (!committed) {
      connection.commit()
      committed = true
    }
  }

  override fun rollback() {
    if (!committed) {
      connection.rollback()
      committed = true
    }
  }

  override fun close() {
    commit()
    connection.close()
    closed = true
  }
}
