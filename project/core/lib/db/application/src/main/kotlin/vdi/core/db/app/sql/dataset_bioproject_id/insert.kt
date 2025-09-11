package vdi.core.db.app.sql.dataset_bioproject_id

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.data.BioprojectIDReference
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.BioprojectIDs} VALUES (?, ?, ?)"

internal fun Connection.insertBioprojectIDs(
  schema: String,
  datasetID: DatasetID,
  projectIDs: Iterable<BioprojectIDReference>,
) =
  withPreparedBatchUpdate(SQL(schema), projectIDs) {
    set(1, datasetID)
    set(2, it.id)
    set(3, it.description)
  }.reduceOrNull(Int::plus) ?: 0