package vdi.core.db.app

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.dataset.insertDataset
import vdi.core.db.app.sql.dataset.updateDataset
import vdi.db.app.TargetDBPlatform
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.InstallTargetID

internal class OracleTransaction(
  project: InstallTargetID,
  schema: String,
  connection: Connection,
): AppDBTransactionImpl(project, schema, connection), AppDBTransaction {
  private val logger = LoggerFactory.getLogger(javaClass)

  override val platform: TargetDBPlatform
    get() = TargetDBPlatform.Oracle

  init {
    connection.autoCommit = false
  }

  override fun upsertDataset(dataset: DatasetRecord) {
    try {
      if (connection.insertDataset(schema, dataset) > 0)
        logger.debug("inserted dataset record for {}", dataset.datasetID)
    } catch (e: SQLException) {
      if (!platform.isUniqueConstraintViolation(e))
        throw e

      if (connection.updateDataset(schema, dataset) > 0)
        logger.debug("updated dataset record for {}", dataset.datasetID)
    }
  }

  override fun upsertDatasetInstallMessage(message: DatasetInstallMessage) {
    try {
      insertDatasetInstallMessage(message)
    } catch (e: SQLException) {
      if (isUniqueConstraintViolation(e))
        updateDatasetInstallMessage(message)
      else
        throw e
    }
  }

  override fun upsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    try {
      insertDatasetMeta(datasetID, meta)
    } catch (e: SQLException) {
      if (isUniqueConstraintViolation(e)) {
        updateDatasetMeta(datasetID, meta)
      } else {
        throw e
      }
    }
  }
}
