package vdi.core.db.app.sql.dataset_source

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.model.meta.DatasetID

// language=sql
private fun sql(schema: String) = "DELETE FROM ${schema}.${Table.ExternalSource} WHERE dataset_id = ?"

internal fun Connection.deleteDatasetSources(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
