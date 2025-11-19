package vdi.core.db.app.sql.dataset_organism

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.model.meta.DatasetID

// language=postgresql
private fun sql(schema: String) = "DELETE FROM ${schema}.${Table.Organisms} WHERE dataset_id = ?"

internal fun Connection.deleteDatasetOrganisms(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
