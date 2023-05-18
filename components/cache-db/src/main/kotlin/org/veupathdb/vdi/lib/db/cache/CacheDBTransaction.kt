package org.veupathdb.vdi.lib.db.cache

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.cache.model.*
import org.veupathdb.vdi.lib.db.cache.sql.insert.*
import org.veupathdb.vdi.lib.db.cache.sql.insert.tryInsertDatasetFiles
import org.veupathdb.vdi.lib.db.cache.sql.insert.tryInsertDatasetProjects
import org.veupathdb.vdi.lib.db.cache.sql.insert.tryInsertDatasetRecord
import org.veupathdb.vdi.lib.db.cache.sql.insert.tryInsertSyncControl
import org.veupathdb.vdi.lib.db.cache.sql.update.*
import org.veupathdb.vdi.lib.db.cache.sql.update.updateDatasetDeleteFlag
import org.veupathdb.vdi.lib.db.cache.sql.update.updateDatasetMeta
import org.veupathdb.vdi.lib.db.cache.sql.update.updateSyncControlData
import org.veupathdb.vdi.lib.db.cache.sql.update.updateSyncControlShare
import org.veupathdb.vdi.lib.db.cache.sql.upsert.upsertDatasetShareOffer
import org.veupathdb.vdi.lib.db.cache.sql.upsert.upsertDatasetShareReceipt
import org.veupathdb.vdi.lib.db.cache.sql.upsert.upsertImportMessages
import java.sql.Connection
import java.time.OffsetDateTime

class CacheDBTransaction(private val connection: Connection) : AutoCloseable {

  private val log = LoggerFactory.getLogger(javaClass)

  private var committed = false

  private var closed = false

  private val con
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      connection

  /**
   * Attempts to insert a dataset record for the given dataset details, aborting
   * the insert query silently if a conflicting record already exists.
   *
   * @param row Row containing the dataset details to insert into the database.
   */
  fun tryInsertDataset(row: Dataset) {
    log.debug("inserting dataset record for dataset {}", row.datasetID)
    con.tryInsertDatasetRecord(row.datasetID, row.typeName, row.typeVersion, row.ownerID, row.isDeleted, row.created)
  }

  /**
   * Attempts to insert dataset metadata for a target dataset, aborting the
   * insert query silently if a record already exists.
   *
   * @param row Dataset metadata to insert.
   */
  fun tryInsertDatasetMeta(row: DatasetMeta) {
    log.debug("inserting metadata for dataset {}", row.datasetID)
    con.tryInsertDatasetMeta(row.datasetID, row.name, row.summary, row.description)
  }

  /**
   * Attempts to insert dataset-to-filename links for a target dataset, aborting
   * any conflicting inserts silently if they exist.
   *
   * @param datasetID ID of the target dataset for which project link records
   * should be inserted.
   *
   * @param files Collection of file names to insert for the target dataset.
   */
  fun tryInsertDatasetFiles(datasetID: DatasetID, files: Collection<String>) {
    log.debug("inserting file links for dataset {}", datasetID)
    con.tryInsertDatasetFiles(datasetID, files)
  }

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
  fun tryInsertDatasetProjects(datasetID: DatasetID, projects: Collection<ProjectID>) {
    log.debug("inserting project links for dataset {}", datasetID)
    con.tryInsertDatasetProjects(datasetID, projects)
  }

  /**
   * Attempts to insert an import control record for the target dataset,
   * aborting the query silently if a conflicting record already exists.
   *
   * @param datasetID ID of the target dataset for which an import control
   * record should be inserted.
   *
   * @param status Import status to set.
   */
  fun tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("inserting import control record for dataset {}", datasetID)
    con.tryInsertImportControl(datasetID, status)
  }

  /**
   * Attempts to insert a sync control record for the target dataset, aborting
   * the query silently if a conflicting record already exists.
   *
   * @param record VDI sync control record to insert.
   */
  fun tryInsertSyncControl(record: VDISyncControlRecord) {
    log.debug("trying to insert a sync control record for dataset {}", record.datasetID)
    return con.tryInsertSyncControl(record)
  }

  fun tryInsertImportMessages(datasetID: DatasetID, messages: String) {
    log.debug("soft-inserting import messages for dataset {}", datasetID)
    con.tryInsertImportMessages(datasetID, messages)
  }

  fun updateImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("updating import status for dataset {} to status {}", datasetID, status)
    con.updateDatasetImportStatus(datasetID, status)
  }

  fun updateDatasetMeta(row: DatasetMeta) {
    log.debug("updating metadata for dataset {}", row.datasetID)
    con.updateDatasetMeta(row.datasetID, row.name, row.summary, row.description)
  }

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
  fun updateMetaSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record meta timestamp for dataset {}", datasetID)
    con.updateSyncControlMeta(datasetID, timestamp)
  }

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
  fun updateDataSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record data timestamp for dataset {}", datasetID)
    con.updateSyncControlData(datasetID, timestamp)
  }

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
  fun updateShareSyncControl(datasetID: DatasetID, timestamp: OffsetDateTime) {
    log.debug("updating sync control record share timestamp for dataset {}", datasetID)
    con.updateSyncControlShare(datasetID, timestamp)
  }

  fun upsertDatasetShareOffer(row: DatasetShareOffer) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareOffer(row.datasetID, row.recipientID, row.action)
  }

  fun upsertDatasetShareReceipt(row: DatasetShareReceipt) {
    log.debug("upserting share receipt action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareReceipt(row.datasetID, row.recipientID, row.action)
  }

  fun upsertImportMessages(datasetID: DatasetID, messages: String) {
    log.debug("upserting import messages for dataset {}", datasetID)
    con.upsertImportMessages(datasetID, messages)
  }

  fun updateDatasetDeleted(datasetID: DatasetID, deleted: Boolean) {
    log.debug("updating dataset deleted flag for dataset {} to {}", datasetID, deleted)
    con.updateDatasetDeleteFlag(datasetID, deleted)
  }

  fun commit() {
    if (!committed) {
      connection.commit()
      committed = true
    }
  }

  fun rollback() {
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
