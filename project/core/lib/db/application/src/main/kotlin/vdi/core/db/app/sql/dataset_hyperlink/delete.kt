package vdi.core.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.model.data.DatasetID

// language=postgresql
private fun sql(schema: String) = "DELETE FROM ${schema}.dataset_hyperlink WHERE dataset_id = ?"

internal fun Connection.deleteDatasetHyperlinks(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
