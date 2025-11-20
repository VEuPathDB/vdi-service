package vdi.core.db.app

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.dataset.upsertDataset
import vdi.core.db.app.sql.dataset_install_message.upsertDatasetInstallMessage
import vdi.core.db.app.sql.dataset_meta.upsertDatasetMeta
import vdi.db.app.TargetDBPlatform
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.InstallTargetID

internal class PostgresTransaction(
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
    if (connection.upsertDataset(schema, dataset) > 0)
      logger.debug("inserted dataset record for {}", dataset.datasetID)
  }

  override fun upsertDatasetInstallMessage(message: DatasetInstallMessage) {
    connection.upsertDatasetInstallMessage(schema, message)
  }

  override fun upsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    connection.upsertDatasetMeta(schema, datasetID, meta)
  }
}
