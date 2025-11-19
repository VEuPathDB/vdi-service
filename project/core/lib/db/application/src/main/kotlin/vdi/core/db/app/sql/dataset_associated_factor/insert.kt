package vdi.core.db.app.sql.dataset_associated_factor

import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.AssociatedFactors} VALUES (?, ?)"

internal fun Connection.insertAssociatedFactors(schema: String, datasetID: DatasetID, factors: Iterable<String>) =
  withPreparedBatchUpdate(SQL(schema), factors) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }.reduceOrNull(Int::plus) ?: 0