package vdi.core.db.app.sql.dataset_country

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.Countries} VALUES (?, ?)"

internal fun Connection.insertCountries(
  schema: String,
  datasetID: DatasetID,
  countries: Iterable<String>,
) =
  withPreparedBatchUpdate(SQL(schema), countries) {
    set(1, datasetID)
    set(2, it)
  }.reduceOrNull(Int::plus) ?: 0