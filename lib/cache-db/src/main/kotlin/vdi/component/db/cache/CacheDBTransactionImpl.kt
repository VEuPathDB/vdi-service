package vdi.component.db.cache

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.cache.model.*
import vdi.component.db.cache.sql.delete.*
import vdi.component.db.cache.sql.insert.*
import vdi.component.db.cache.sql.update.*
import vdi.component.db.cache.sql.upsert.upsertDatasetShareOffer
import vdi.component.db.cache.sql.upsert.upsertDatasetShareReceipt
import vdi.component.db.cache.sql.upsert.upsertImportControl
import vdi.component.db.cache.sql.upsert.upsertImportMessages
import java.sql.Connection
import java.time.OffsetDateTime

internal class CacheDBTransactionImpl(private val connection: Connection) : CacheDBTransaction {

  private val log = LoggerFactory.getLogger(javaClass)

  private var committed = false

  private var closed = false

  private val con
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      connection

  // region Delete

  override fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID) {
    log.debug("deleting share offer record for dataset {}, recipient {}", datasetID, recipientID)
    con.deleteShareOffer(datasetID, recipientID)
  }

  override fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) {
    log.debug("deleting share receipt record for dataset {}, recipient {}", datasetID, recipientID)
    con.deleteShareReceipt(datasetID, recipientID)
  }

  override fun deleteDatasetMetadata(datasetID: DatasetID) {
    log.debug("deleting dataset metadata for dataset {}", datasetID)
    con.deleteDatasetMetadata(datasetID)
  }

  override fun deleteDatasetProjects(datasetID: DatasetID) {
    log.debug("deleting dataset project links for dataset {}", datasetID)
    con.deleteDatasetProjects(datasetID)
  }

  override fun deleteDatasetShareOffers(datasetID: DatasetID) {
    log.debug("deleting dataset share offers for dataset {}", datasetID)
    con.deleteDatasetShareOffers(datasetID)
  }

  override fun deleteDatasetShareReceipts(datasetID: DatasetID) {
    log.debug("deleting dataset share receipts for dataset {}", datasetID)
    con.deleteDatasetShareReceipts(datasetID)
  }

  override fun deleteDataset(datasetID: DatasetID) {
    log.debug("deleting dataset {}", datasetID)
    con.deleteDataset(datasetID)
  }

  override fun deleteImportControl(datasetID: DatasetID) {
    log.debug("deleting import control record for dataset {}", datasetID)
    con.deleteImportControl(datasetID)
  }

  override fun deleteImportMessages(datasetID: DatasetID) {
    log.debug("deleting import messages for dataset {}", datasetID)
    con.deleteImportMessages(datasetID)
  }

  override fun deleteSyncControl(datasetID: DatasetID) {
    log.debug("deleting sync control record for dataset {}", datasetID)
    con.deleteSyncControl(datasetID)
  }

  override fun deleteInstallFiles(datasetID: DatasetID) {
    log.debug("deleting install files for dataset {}", datasetID)
    con.deleteInstallFiles(datasetID)
  }

  override fun deleteUploadFiles(datasetID: DatasetID) {
    log.debug("deleting upload files for dataset {}", datasetID)
    con.deleteUploadFiles(datasetID)
  }

  // endregion Delete

  // region Insert

  // endregion Insert

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

  override fun tryInsertSyncControl(record: VDISyncControlRecord) {
    if (con.tryInsertSyncControl(record) > 0)
      log.debug("inserted sync control record for dataset {}", record.datasetID)
  }

  override fun tryInsertImportMessages(datasetID: DatasetID, messages: String) {
    if (con.tryInsertImportMessages(datasetID, messages) > 0)
      log.debug("inserted import messages for dataset {}", datasetID)
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
