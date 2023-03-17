package vdi.component.db.cache

import org.slf4j.LoggerFactory
import org.veupathdb.service.vdi.db.internal.model.*
import org.veupathdb.service.vdi.db.internal.queries.*
import org.veupathdb.service.vdi.generated.model.DatasetImportStatus
import java.sql.Connection
import vdi.component.db.cache.model.*
import vdi.component.db.cache.sql.*
import vdi.component.db.cache.sql.insertDatasetFiles
import vdi.component.db.cache.sql.updateDatasetMeta
import vdi.component.db.cache.sql.upsertDatasetShareOffer
import vdi.components.common.fields.DatasetID

class CacheDBTransaction(private val connection: Connection) : AutoCloseable {

  private val log = LoggerFactory.getLogger(javaClass)

  private var committed = false

  private var closed = false

  private val con
    get() = if (committed || closed)
      throw IllegalStateException("cannot execute queries on a connection that has already been closed or committed")
    else
      connection

  fun insertDataset(row: Dataset) {
    log.debug("inserting dataset record for dataset {}", row.datasetID)
    con.insertDatasetRecord(row.datasetID, row.typeName, row.typeVersion, row.ownerID, row.isDeleted, row.created)
  }

  fun insertDatasetMeta(row: DatasetMeta) {
    log.debug("inserting metadata for dataset {}", row.datasetID)
    con.insertDatasetMeta(row.datasetID, row.name, row.summary, row.description)
  }

  fun updateDatasetMeta(row: DatasetMeta) {
    log.debug("updating metadata for dataset {}", row.datasetID)
    con.updateDatasetMeta(row.datasetID, row.name, row.summary, row.description)
  }

  fun insertDatasetFiles(row: DatasetFileLinks) {
    log.debug("inserting file links for dataset {}", row.datasetID)
    con.insertDatasetFiles(row.datasetID, row.files)
  }

  fun insertDatasetProjects(row: DatasetProjectLinks) {
    log.debug("inserting project links for dataset {}", row.datasetID)
    con.insertDatasetProjects(row.datasetID, row.projects)
  }

  fun insertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
    log.debug("inserting import control record for dataset {}", datasetID)
    con.insertImportControl(datasetID, status)
  }

  fun upsertDatasetShareOffer(row: DatasetShareOffer) {
    log.debug("upserting share offer action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareOffer(row.datasetID, row.recipientID, row.action)
  }

  fun upsertDatasetShareReceipt(row: DatasetShareReceipt) {
    log.debug("upserting share receipt action {} for dataset {}, recipient {}", row.action, row.datasetID, row.recipientID)
    con.upsertDatasetShareReceipt(row.datasetID, row.recipientID, row.action)
  }

  fun commit() {
    if (!committed) {
      con.commit()
      committed = true
    }
  }

  fun rollback() {
    if (!committed) {
      con.rollback()
      committed = true
    }
  }

  override fun close() {
    commit()
    con.close()
    closed = true
  }
}

