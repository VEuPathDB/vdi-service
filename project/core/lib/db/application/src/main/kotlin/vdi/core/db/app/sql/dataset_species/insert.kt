package vdi.core.db.app.sql.dataset_species

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=sql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.Species} VALUES (?, ?)"

internal fun Connection.insertSpecies(
  schema: String,
  datasetID: DatasetID,
  species: Iterable<String>,
) =
  withPreparedBatchUpdate(SQL(schema), species) {
    set(1, datasetID)
    set(2, it)
  }.reduceOrNull(Int::plus) ?: 0