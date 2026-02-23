package vdi.core.db.app.sql.dataset_outcome

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.Outcomes} VALUES (?, ?)"

internal fun Connection.insertOutcomes(
  schema: String,
  datasetID: DatasetID,
  outcomes: Iterable<String>,
) =
  withPreparedBatchUpdate(SQL(schema), outcomes) {
    set(1, datasetID)
    set(2, it)
  }.reduceOrNull(Int::plus) ?: 0