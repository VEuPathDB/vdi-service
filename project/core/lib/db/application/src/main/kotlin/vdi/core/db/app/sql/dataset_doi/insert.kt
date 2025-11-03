package vdi.core.db.app.sql.dataset_doi

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.data.DOIReference
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.DOIs} VALUES (?, ?, ?)"

internal fun Connection.insertDOIs(
  schema: String,
  datasetID: DatasetID,
  dois: Iterable<DOIReference>,
) =
  withPreparedBatchUpdate(SQL(schema), dois) {
    set(1, datasetID)
    set(2, it.doi)
    set(3, it.description)
  }.reduceOrNull(Int::plus) ?: 0