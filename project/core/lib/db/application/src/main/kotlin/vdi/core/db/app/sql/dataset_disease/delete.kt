package vdi.core.db.app.sql.dataset_disease

import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "DELETE FROM ${schema}.dataset_disease WHERE dataset_id = ?"

internal fun Connection.deleteDiseases(schema: String, datasetID: DatasetID) =
  usingPreparedUpdate(SQL(schema)) { it[1] = datasetID }