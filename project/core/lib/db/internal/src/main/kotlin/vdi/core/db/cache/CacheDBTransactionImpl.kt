package vdi.core.db.cache

import java.sql.Connection
import java.time.OffsetDateTime
import javax.sql.DataSource
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.sql.dataset_metadata.deleteDatasetMetadata
import vdi.core.db.cache.sql.dataset_metadata.tryInsertDatasetMeta
import vdi.core.db.cache.sql.dataset_metadata.updateDatasetMeta
import vdi.core.db.cache.sql.dataset_projects.deleteDatasetProjects
import vdi.core.db.cache.sql.dataset_projects.tryInsertDatasetProjects
import vdi.core.db.cache.sql.dataset_revisions.deleteDatasetRevisions
import vdi.core.db.cache.sql.dataset_revisions.tryInsertDatasetRevision
import vdi.core.db.cache.sql.dataset_revisions.tryInsertDatasetRevisions
import vdi.core.db.cache.sql.dataset_share_offers.deleteDatasetShareOffers
import vdi.core.db.cache.sql.dataset_share_offers.deleteShareOffer
import vdi.core.db.cache.sql.dataset_share_offers.upsertDatasetShareOffer
import vdi.core.db.cache.sql.dataset_share_receipts.deleteDatasetShareReceipts
import vdi.core.db.cache.sql.dataset_share_receipts.deleteShareReceipt
import vdi.core.db.cache.sql.dataset_share_receipts.upsertDatasetShareReceipt
import vdi.core.db.cache.sql.datasets.deleteDataset
import vdi.core.db.cache.sql.datasets.tryInsertDatasetRecord
import vdi.core.db.cache.sql.datasets.updateDatasetDeleteFlag
import vdi.core.db.cache.sql.import_control.deleteImportControl
import vdi.core.db.cache.sql.import_control.tryInsertImportControl
import vdi.core.db.cache.sql.import_control.updateDatasetImportStatus
import vdi.core.db.cache.sql.import_control.upsertImportControl
import vdi.core.db.cache.sql.import_messages.deleteImportMessages
import vdi.core.db.cache.sql.import_messages.tryInsertImportMessages
import vdi.core.db.cache.sql.import_messages.upsertImportMessages
import vdi.core.db.cache.sql.install_files.deleteInstallFiles
import vdi.core.db.cache.sql.install_files.tryInsertInstallFiles
import vdi.core.db.cache.sql.sync_control.*
import vdi.core.db.cache.sql.upload_files.deleteUploadFiles
import vdi.core.db.cache.sql.upload_files.tryInsertUploadFiles
import vdi.core.db.model.SyncControlRecord
import vdi.logging.logger
import vdi.model.data.*

internal class CacheDBTransactionImpl(
  override val dataSource: DataSource,
  override val connection: Connection,
  override val details: CacheDBConnectionDetails,
): CacheDBTransaction, CacheDBImplBase() {
  private val log = logger<CacheDBTransaction>()

  private var committed = false

  private var closed = false

  private val con
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      connection

  // region Delete

  override fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID) =
    con.deleteShareOffer(datasetID, recipientID).also { if (it)
      log.debug("deleted share offer for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) =
    con.deleteShareReceipt(datasetID, recipientID).also { if (it)
      log.debug("deleted share receipt for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteDatasetMetadata(datasetID: DatasetID) =
    con.deleteDatasetMetadata(datasetID).also { if (it)
      log.debug("deleted metadata for dataset {}", datasetID) }

  override fun deleteInstallTargetLinks(datasetID: DatasetID) =
    con.deleteDatasetProjects(datasetID).also { if (it > 0)
      log.debug("deleted {} install target links for dataset {}", it, datasetID) }

  override fun deleteShareOffers(datasetID: DatasetID) =
    con.deleteDatasetShareOffers(datasetID).also { if (it > 0)
      log.debug("deleted {} share offers for dataset {}", it, datasetID) }

  override fun deleteShareReceipts(datasetID: DatasetID) =
    con.deleteDatasetShareReceipts(datasetID).also { if (it > 0)
      log.debug("deleted {} share receipts for dataset {}", it, datasetID) }

  override fun deleteDataset(datasetID: DatasetID) =
    con.deleteDataset(datasetID).also { if (it > 0)
      log.info("deleted {} records for dataset {}", it, datasetID) }

  override fun deleteImportControl(datasetID: DatasetID) =
    (con.deleteImportControl(datasetID) > 0).also { if (it)
      log.debug("deleted import control record for dataset {}", datasetID) }

  override fun deleteImportMessages(datasetID: DatasetID) =
    con.deleteImportMessages(datasetID).also { if (it > 0)
      log.debug("deleted {} import messages for dataset {}", it, datasetID) }

  override fun deleteSyncControl(datasetID: DatasetID) =
    (con.deleteSyncControl(datasetID) > 0).also { if (it)
      log.debug("deleted sync control record for dataset {}", datasetID) }

  override fun deleteInstallFiles(datasetID: DatasetID) =
    con.deleteInstallFiles(datasetID).also { if (it > 0)
      log.debug("deleted {} install files for dataset {}", it, datasetID) }

  override fun deleteUploadFiles(datasetID: DatasetID) =
    con.deleteUploadFiles(datasetID).also { if (it > 0)
      log.debug("deleted {} upload files for dataset {}", it, datasetID) }

  override fun deleteRevisions(originalID: DatasetID) =
    con.deleteDatasetRevisions(originalID).also { if (it > 0)
      log.debug("deleted {} revisions for dataset {}", it, originalID) }

  // endregion Delete

  // region Try-Insert

  override fun tryInsertDataset(row: Dataset) =
    (con.tryInsertDatasetRecord(row) > 0).also { if (it)
      log.debug("inserted dataset record for dataset {}", row.datasetID) }

  override fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    con.tryInsertInstallFiles(datasetID, files).also { if (it > 0)
      log.debug("inserted {} dataset install files for dataset {}", it, datasetID) }

  override fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    con.tryInsertUploadFiles(datasetID, files).also { if (it > 0)
      log.debug("inserted {} dataset upload files for dataset {}", it, datasetID) }

  override fun tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    (con.tryInsertDatasetMeta(datasetID, meta) > 0).also { if (it)
      log.debug("inserted metadata for dataset {}", datasetID) }

  override fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<InstallTargetID>) =
    con.tryInsertDatasetProjects(datasetID, projects).also { if (it > 0)
      log.debug("inserted {} install target links for dataset {}", it, datasetID) }

  override fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (con.tryInsertImportControl(datasetID, status) > 0).also { if (it)
      log.debug("inserted import control record for dataset {}", datasetID) }

  override fun tryInsertSyncControl(record: SyncControlRecord) =
    (con.tryInsertSyncControl(record) > 0).also { if (it)
      log.debug("inserted sync control record for dataset {}", record.datasetID) }

  override fun tryInsertImportMessages(datasetID: DatasetID, messages: Iterable<String>) =
    con.tryInsertImportMessages(datasetID, messages).also { if (it > 0)
      log.debug("inserted {} import messages for dataset {}", it, datasetID) }

  override fun tryInsertRevisionLink(originalID: DatasetID, revision: DatasetRevision) =
    (con.tryInsertDatasetRevision(originalID, revision) > 0).also { if (it)
      log.debug("inserted revision link from dataset {} to revision {}", originalID, revision.revisionID) }

  override fun tryInsertRevisionLinks(originalID: DatasetID, revisions: Iterable<DatasetRevision>) =
    con.tryInsertDatasetRevisions(originalID, revisions).also { if (it > 0)
      log.debug("inserted {} revision links for dataset {}", it, originalID) }

  // endregion Try-Insert

  override fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (con.updateDatasetImportStatus(datasetID, status) > 0).also { if (it)
      log.debug("updated import status for dataset {} to status {}", datasetID, status) }

  override fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean) =
    (con.updateDatasetDeleteFlag(datasetID, deleted) > 0).also { if (it)
      log.debug("updating dataset deleted flag for dataset {} to {}", datasetID, deleted) }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    (con.updateDatasetMeta(datasetID, meta) > 0).also { if (it)
      log.debug("updated metadata for dataset {}", datasetID) }

  override fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (con.updateSyncControlMeta(datasetID, timestamp) > 0).also { if (it)
      log.debug("updated sync control record meta timestamp for dataset {}", datasetID) }

  override fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (con.updateSyncControlData(datasetID, timestamp) > 0).also { if (it)
      log.debug("updated sync control record data timestamp for dataset {}", datasetID) }

  override fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (con.updateSyncControlShare(datasetID, timestamp) > 0).also { if (it)
      log.debug("updated sync control record share timestamp for dataset {}", datasetID) }

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
