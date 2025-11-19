package vdi.core.db.app.sql.dataset_country

import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "DELETE FROM ${schema}.${Table.Countries} WHERE dataset_id = ?"

internal fun Connection.deleteCountries(schema: String, datasetID: DatasetID) =
  usingPreparedUpdate(SQL(schema)) { it[1] = datasetID }