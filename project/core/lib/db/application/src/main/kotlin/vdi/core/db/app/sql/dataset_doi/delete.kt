package vdi.core.db.app.sql.dataset_doi

import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "DELETE FROM ${schema}.dataset_doi WHERE dataset_id = ?"

internal fun Connection.deleteDOIs(schema: String, datasetID: DatasetID) =
  usingPreparedUpdate(SQL(schema)) { it[1] = datasetID }