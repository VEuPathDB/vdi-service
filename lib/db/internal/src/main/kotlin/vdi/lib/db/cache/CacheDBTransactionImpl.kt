package vdi.lib.db.cache

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.lib.db.cache.model.Dataset
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.model.DatasetShareOffer
import vdi.lib.db.cache.model.DatasetShareReceipt
import vdi.lib.db.cache.sql.delete.*
import vdi.lib.db.cache.sql.insert.*
import vdi.lib.db.cache.sql.update.*
import vdi.lib.db.cache.sql.upsert.upsertDatasetShareOffer
import vdi.lib.db.cache.sql.upsert.upsertDatasetShareReceipt
import vdi.lib.db.cache.sql.upsert.upsertImportControl
import vdi.lib.db.cache.sql.upsert.upsertImportMessages
import vdi.lib.db.model.SyncControlRecord
import vdi.lib.logging.logger

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

  override fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>) {
    if (con.tryInsertInstallFiles(datasetID, files) > 0)
      log.debug("inserted dataset install files for dataset {}", datasetID)
  }

  override fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>) {
    if (con.tryInsertUploadFiles(datasetID, files) > 0)
      log.debug("inserted dataset upload files for dataset {}", datasetID)
  }

  override fun tryInsertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) {
    if (con.tryInsertDatasetMeta(datasetID, meta) > 0)
      log.debug("inserted metadata for dataset {}", datasetID)
  }

  override fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Collection<ProjectID>) {
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

  override fun tryInsertRevisionLink(originalID: DatasetID, revision: VDIDatasetRevision) {
    if (con.tryInsertDatasetRevision(originalID, revision) > 0)
      log.debug("inserted revision link from dataset {} to revision {}", originalID, revision.revisionID)
  }

  override fun tryInsertRevisionLinks(originalID: DatasetID, revisions: Iterable<VDIDatasetRevision>) {
    val count = con.tryInsertDatasetRevisions(originalID, revisions)
    if (count > 0)
      log.debug("inserted {} revision links for dataset {}", count, originalID)
  }

  // endregion Try-Insert

  override fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("updating import status for dataset {} to status {}", datasetID, status)
    con.updateDatasetImportStatus(datasetID, status)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) {
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

  override fun upsertDatasetShareOffer(row: DatasetShareOffer) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareOffer(row.datasetID, row.recipientID, row.action)
  }

  override fun upsertDatasetShareReceipt(row: DatasetShareReceipt) {
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
