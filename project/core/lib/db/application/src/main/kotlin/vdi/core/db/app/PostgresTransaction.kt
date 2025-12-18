package vdi.core.db.app

import java.sql.Connection
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.dataset.upsertDataset
import vdi.core.db.app.sql.dataset_install_message.upsertDatasetInstallMessage
import vdi.core.db.app.sql.dataset_meta.upsertDatasetMeta
import vdi.db.app.TargetDBPlatform
import vdi.logging.logger
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.InstallTargetID

internal class PostgresTransaction(
  project: InstallTargetID,
  schema: String,
  connection: Connection,
): AppDBTransactionImpl(project, schema, connection), AppDBTransaction {
  private val logger = logger("AppDB")

  override val platform: TargetDBPlatform
    get() = TargetDBPlatform.Postgres

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
