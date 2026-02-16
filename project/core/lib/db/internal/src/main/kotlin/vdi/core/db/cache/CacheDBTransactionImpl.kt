package vdi.core.db.cache

import java.sql.Connection
import java.time.OffsetDateTime
import javax.sql.DataSource
import vdi.core.db.cache.model.Dataset
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.ShareOfferRecord
import vdi.core.db.cache.model.ShareReceiptRecord
import vdi.core.db.cache.sql.simple.*
import vdi.core.db.model.SyncControlRecord
import vdi.logging.logger
import vdi.model.DatasetUploadStatus
import vdi.model.meta.*

internal class CacheDBTransactionImpl(
  override val dataSource: DataSource,
  private val con: Connection,
  override val details: CacheDBConnectionDetails,
): CacheDBTransaction, CacheDBImplBase() {
  private val log = logger("CacheDB")

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
    runQuery { DatasetShareOffersTable.delete(datasetID, recipientID) }.also { if (it)
      log.debug("deleted share offer for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) =
    runQuery { DatasetShareReceiptsTable.delete(datasetID, recipientID) }.also { if (it)
      log.debug("deleted share receipt for dataset {}, recipient {}", datasetID, recipientID) }

  override fun deleteImportMessages(datasetID: DatasetID) =
    runQuery { ImportMessagesTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} import messages for dataset {}", it, datasetID) }

  override fun deleteRevisions(originalID: DatasetID) =
    runQuery { DatasetRevisionsTable.delete(originalID) }.also { if (it > 0)
      log.debug("deleted {} revisions for dataset {}", it, originalID) }

  override fun deletePublication(datasetID: DatasetID, publicationID: String) =
    (runQuery { DatasetPublicationsTable.delete(datasetID, publicationID) } > 0).also { if (it)
      log.debug("deleted publication {} reference from dataset {}", publicationID, datasetID) }

  override fun deletePublications(datasetID: DatasetID) =
    runQuery { DatasetPublicationsTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} publication references from dataset {}", it, datasetID) }

  override fun deleteShareOffers(datasetID: DatasetID): Int =
    runQuery { DatasetShareOffersTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} share offers for dataset {}", it, datasetID) }

  override fun deleteShareReceipts(datasetID: DatasetID): Int =
    runQuery { DatasetShareReceiptsTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} share receipts for dataset {}", it, datasetID) }

  override fun deleteInstallFiles(datasetID: DatasetID): Int =
    runQuery { DatasetInstallFiles.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} install file records for dataset {}", it, datasetID) }

  override fun deleteUploadFiles(datasetID: DatasetID): Int =
    runQuery { DatasetUploadFiles.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} upload file records for dataset {}", it, datasetID) }

  override fun deleteImportControlRecords(datasetID: DatasetID): Int =
    runQuery { ImportControlTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} import control records for dataset {}", it, datasetID) }

  override fun deleteSyncControlRecords(datasetID: DatasetID): Int =
    runQuery { SyncControlTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} sync control records for dataset {}", it, datasetID) }

  override fun deleteDatasetMetadata(datasetID: DatasetID): Boolean =
    runQuery { DatasetMetadataTable.delete(datasetID) > 0 }.also { if (it)
      log.debug("deleted metadata record for dataset {}", datasetID) }

  override fun deleteDatasetInstallTargets(datasetID: DatasetID): Int =
    runQuery { DatasetProjectsTable.delete(datasetID) }.also { if (it > 0)
      log.debug("deleted {} install target links for dataset {}", it, datasetID) }

  // endregion Delete

  // region Try-Insert

  override fun tryInsertDataset(row: Dataset) =
    (runQuery { DatasetsTable.tryInsert(row) } > 0).also { if (it)
      log.debug("inserted dataset record for dataset {}", row.datasetID) }

  override fun tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    runQuery { DatasetInstallFiles.tryInsert(datasetID, files) }.also { if (it > 0)
      log.debug("inserted {} dataset install files for dataset {}", it, datasetID) }

  override fun tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
    runQuery { DatasetUploadFiles.tryInsert(datasetID, files) }.also { if (it > 0)
      log.debug("inserted {} dataset upload files for dataset {}", it, datasetID) }

  override fun tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    runQuery { DatasetMetadataTable.tryInsert(datasetID, meta) > 0 }.also { if (it)
      log.debug("inserted metadata for dataset {}", datasetID) }

  override fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<InstallTargetID>) =
    runQuery { DatasetProjectsTable.tryInsert(datasetID, projects) }.also { if (it > 0)
      log.debug("inserted {} install target links for dataset {}", it, datasetID) }

  override fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (runQuery { ImportControlTable.tryInsert(datasetID, status) } > 0).also { if (it)
      log.debug("inserted import control record for dataset {}", datasetID) }

  override fun tryInsertSyncControl(record: SyncControlRecord) =
    (runQuery { SyncControlTable.tryInsert(record) } > 0).also { if (it)
      log.debug("inserted sync control record for dataset {}", record.datasetID) }

  override fun tryInsertImportMessages(datasetID: DatasetID, messages: Iterable<String>) =
    runQuery { ImportMessagesTable.tryInsert(datasetID, messages) }.also { if (it > 0)
      log.debug("inserted {} import messages for dataset {}", it, datasetID) }

  override fun tryInsertRevisionLink(originalID: DatasetID, revision: DatasetRevision) =
    (runQuery { DatasetRevisionsTable.tryInsert(originalID, revision) } > 0).also { if (it)
      log.debug("inserted revision link from dataset {} to revision {}", originalID, revision.revisionID) }

  override fun tryInsertRevisionLinks(history: DatasetRevisionHistory) =
    runQuery { DatasetRevisionsTable.tryInsert(history.originalID, history.revisions) }.also { if (it > 0)
      log.debug("inserted {} revision links for dataset {}", it, history.originalID) }

  override fun tryInsertPublications(datasetID: DatasetID, publications: Iterable<DatasetPublication>) =
    runQuery { DatasetPublicationsTable.tryInsert(datasetID, publications) }.also { if (it > 0)
      log.debug("inserted {} publication records for dataset {}", it, datasetID) }

  // endregion Try-Insert

  override fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
    (runQuery { ImportControlTable.update(datasetID, status) } > 0).also { if (it)
      log.debug("updated import status for dataset {} to status {}", datasetID, status) }

  override fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean) =
    (runQuery { DatasetsTable.update(datasetID, deleted) } > 0).also { if (it)
      log.debug("updating dataset deleted flag for dataset {} to {}", datasetID, deleted) }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
    (runQuery { DatasetMetadataTable.update(datasetID, meta) } > 0).also { if (it)
      log.debug("updated metadata for dataset {}", datasetID) }

  override fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { SyncControlTable.updateMetaTime(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record meta timestamp for dataset {}", datasetID) }

  override fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { SyncControlTable.updateDataTime(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record data timestamp for dataset {}", datasetID) }

  override fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) =
    (runQuery { SyncControlTable.updateShareTime(datasetID, timestamp) } > 0).also { if (it)
      log.debug("updated sync control record share timestamp for dataset {}", datasetID) }

  override fun upsertDatasetShareOffer(row: ShareOfferRecord) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    runQuery { DatasetShareOffersTable.upsert(row.datasetID, row.recipientID, row.action) }
  }

  override fun upsertDatasetShareReceipt(row: ShareReceiptRecord) {
    log.debug("upserting share receipt action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    runQuery { DatasetShareReceiptsTable.upsert(row.datasetID, row.recipientID, row.action) }
  }

  override fun upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("upserting import control record for dataset {} for status {}", datasetID, status)
    runQuery { ImportControlTable.upsert(datasetID, status) }
  }

  override fun upsertUploadStatus(datasetID: DatasetID, status: DatasetUploadStatus) {
    log.debug("upserting upload status record for dataset {} for status {}", datasetID, status)
    runQuery { UploadStatus.upsert(datasetID, status) }
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
