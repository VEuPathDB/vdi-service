package vdi.core.db.app.sql.dataset_disease

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.Diseases} VALUES (?, ?)"

internal fun Connection.insertDiseases(
  schema: String,
  datasetID: DatasetID,
  diseases: Iterable<String>,
) =
  withPreparedBatchUpdate(SQL(schema), diseases) {
    set(1, datasetID)
    set(2, it)
  }.reduceOrNull(Int::plus) ?: 0