package vdi.core.db.app.sql.dataset_sample_type

import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "DELETE FROM ${schema}.${Table.SampleTypes} WHERE dataset_id = ?"

internal fun Connection.deleteSampleTypes(schema: String, datasetID: DatasetID) =
  usingPreparedUpdate(SQL(schema)) { it[1] = datasetID }