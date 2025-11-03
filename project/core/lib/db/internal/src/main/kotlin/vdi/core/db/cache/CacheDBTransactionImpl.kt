package vdi.core.db.cache

import java.sql.Connection
import java.time.OffsetDateTime
import javax.sql.DataSource
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.sql.dataset_metadata.tryInsertDatasetMeta
import vdi.core.db.cache.sql.dataset_metadata.updateDatasetMeta
import vdi.core.db.cache.sql.dataset_projects.tryInsertDatasetProjects
import vdi.core.db.cache.sql.dataset_publications.deleteAllPublicationsForDataset
import vdi.core.db.cache.sql.dataset_publications.deleteDatasetPublication
import vdi.core.db.cache.sql.dataset_publications.tryInsertPublications
import vdi.core.db.cache.sql.dataset_revisions.deleteDatasetRevisions
import vdi.core.db.cache.sql.dataset_revisions.tryInsertDatasetRevision
import vdi.core.db.cache.sql.dataset_revisions.tryInsertDatasetRevisions
import vdi.core.db.cache.sql.dataset_share_offers.deleteShareOffer
import vdi.core.db.cache.sql.dataset_share_offers.upsertDatasetShareOffer
import vdi.core.db.cache.sql.dataset_share_receipts.deleteShareReceipt
import vdi.core.db.cache.sql.dataset_share_receipts.upsertDatasetShareReceipt
import vdi.core.db.cache.sql.datasets.deleteDataset
import vdi.core.db.cache.sql.datasets.tryInsertDatasetRecord
import vdi.core.db.cache.sql.datasets.updateDatasetDeleteFlag
import vdi.core.db.cache.sql.import_control.tryInsertImportControl
import vdi.core.db.cache.sql.import_control.updateDatasetImportStatus
import vdi.core.db.cache.sql.import_control.upsertImportControl
import vdi.core.db.cache.sql.import_messages.deleteImportMessages
import vdi.core.db.cache.sql.import_messages.upsertImportMessages
import vdi.core.db.cache.sql.install_files.tryInsertInstallFiles
import vdi.core.db.cache.sql.sync_control.tryInsertSyncControl
import vdi.core.db.cache.sql.sync_control.updateSyncControlData
import vdi.core.db.cache.sql.sync_control.updateSyncControlMeta
import vdi.core.db.cache.sql.sync_control.updateSyncControlShare
import vdi.core.db.cache.sql.upload_files.tryInsertUploadFiles
import vdi.core.db.model.SyncControlRecord
import vdi.logging.logger
import vdi.model.data.*

internal class CacheDBTransactionImpl(
  override val dataSource: DataSource,
  private val con: Connection,
  override val details: CacheDBConnectionDetails,
): CacheDBTransaction, CacheDBImplBase() {
  private val log = logger<CacheDBTransaction>()

  private var committed = false

  private var closed = false

  override val connection
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      con

  override fun <T> runQuery(fn: Connection.() -> T): T = connection.fn()

  // region Delete

  override fun deleteShareOffer(datasetID: DatasetID, recipientID: UserID) =
    runQuery { deleteShareOffer(datasetID, recipientID) }.also { if (it)
      log.debug("deleted share offer for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) =
    runQuery { deleteShareReceipt(datasetID, recipientID) }.also { if (it)
      log.debug("deleted share receipt for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteDataset(datasetID: DatasetID) =
    runQuery { deleteDataset(datasetID) }.also { if (it > 0)
      log.info("deleted {} records for dataset {}", it, datasetID) }

  override fun deleteImportMessages(datasetID: DatasetID) =
    runQuery { deleteImportMessages(datasetID) }.also { if (it > 0)
      log.debug("deleted {} import messages for dataset {}", it, datasetID) }

  override fun deleteRevisions(originalID: DatasetID) =
    runQuery { deleteDatasetRevisions(originalID) }.also { if (it > 0)
      log.debug("deleted {} revisions for dataset {}", it, originalID) }

  override fun deletePublication(datasetID: DatasetID, publicationID: String) =
    (runQuery { deleteDatasetPublication(datasetID, publicationID) } > 0).also { if (it)
      log.debug("deleted publication {} reference from dataset {}", publicationID, datasetID) }

  override fun deletePublications(datasetID: DatasetID) =
    runQuery { deleteAllPublicationsForDataset(datasetID) }.also { if (it > 0)
      log.debug("deleted {} publication references from dataset {}", it, datasetID) }

  // endregion Delete

  // region Try-Insert

  override fun tryInsertDataset(row: Dataset) =
    (runQuery { tryInsertDatasetRecord(row) } > 0).also { if (it)
      log.debug("inserted dataset record for dataset {}", row.datasetID) }

  override fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    runQuery { tryInsertInstallFiles(datasetID, files) }.also { if (it > 0)
      log.debug("inserted {} dataset install files for dataset {}", it, datasetID) }

  override fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    runQuery { tryInsertUploadFiles(datasetID, files) }.also { if (it > 0)
      log.debug("inserted {} dataset upload files for dataset {}", it, datasetID) }

  override fun tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    (runQuery { tryInsertDatasetMeta(datasetID, meta) } > 0).also { if (it)
      log.debug("inserted metadata for dataset {}", datasetID) }

  override fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<InstallTargetID>) =
    runQuery { tryInsertDatasetProjects(datasetID, projects) }.also { if (it > 0)
      log.debug("inserted {} install target links for dataset {}", it, datasetID) }

  override fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (runQuery { tryInsertImportControl(datasetID, status) } > 0).also { if (it)
      log.debug("inserted import control record for dataset {}", datasetID) }

  override fun tryInsertSyncControl(record: SyncControlRecord) =
    (runQuery { tryInsertSyncControl(record) } > 0).also { if (it)
      log.debug("inserted sync control record for dataset {}", record.datasetID) }

  override fun tryInsertImportMessages(datasetID: DatasetID, messages: Iterable<String>) =
    runQuery { upsertImportMessages(datasetID, messages) }.also { if (it > 0)
      log.debug("inserted {} import messages for dataset {}", it, datasetID) }

  override fun tryInsertRevisionLink(originalID: DatasetID, revision: DatasetRevision) =
    (runQuery { tryInsertDatasetRevision(originalID, revision) } > 0).also { if (it)
      log.debug("inserted revision link from dataset {} to revision {}", originalID, revision.revisionID) }

  override fun tryInsertRevisionLinks(history: DatasetRevisionHistory) =
    runQuery { tryInsertDatasetRevisions(history.originalID, history.revisions) }.also { if (it > 0)
      log.debug("inserted {} revision links for dataset {}", it, history.originalID) }

  override fun tryInsertPublications(datasetID: DatasetID, publications: Iterable<DatasetPublication>) =
    runQuery { tryInsertPublications(datasetID, publications) }.also { if (it > 0)
      log.debug("inserted {} publication records for dataset {}", it, datasetID) }

  // endregion Try-Insert

  override fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (runQuery { updateDatasetImportStatus(datasetID, status) } > 0).also { if (it)
      log.debug("updated import status for dataset {} to status {}", datasetID, status) }

  override fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean) =
    (runQuery { updateDatasetDeleteFlag(datasetID, deleted) } > 0).also { if (it)
      log.debug("updating dataset deleted flag for dataset {} to {}", datasetID, deleted) }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    (runQuery { updateDatasetMeta(datasetID, meta) } > 0).also { if (it)
      log.debug("updated metadata for dataset {}", datasetID) }

  override fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { updateSyncControlMeta(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record meta timestamp for dataset {}", datasetID) }

  override fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { updateSyncControlData(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record data timestamp for dataset {}", datasetID) }

  override fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { updateSyncControlShare(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record share timestamp for dataset {}", datasetID) }

  override fun upsertDatasetShareOffer(row: ShareOfferRecord) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    runQuery { upsertDatasetShareOffer(row.datasetID, row.recipientID, row.action) }
  }

  override fun upsertDatasetShareReceipt(row: ShareReceiptRecord) {
    log.debug("upserting share receipt action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    runQuery { upsertDatasetShareReceipt(row.datasetID, row.recipientID, row.action) }
  }

  override fun upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("upserting import control record for dataset {} for status {}", datasetID, status)
    runQuery { upsertImportControl(datasetID, status) }
  }

  override fun upsertImportMessages(datasetID: DatasetID, messages: Iterable<String>) {
    log.debug("upserting import messages for dataset {}", datasetID)
    runQuery { upsertImportMessages(datasetID, messages) }
  }

  override fun commit() {
    if (!committed) {
      con.commit()
      committed = true
    }
  }

  override fun rollback() {
    if (!committed) {
      con.rollback()
      committed = true
    }
  }

  override fun close() {
    commit()
    if (!closed) {
      con.close()
      closed = true
    }
  }
}
