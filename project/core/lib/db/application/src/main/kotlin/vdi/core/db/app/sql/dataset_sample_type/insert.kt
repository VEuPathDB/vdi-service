package vdi.core.db.app.sql.dataset_sample_type

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.SampleTypes} VALUES (?, ?)"

internal fun Connection.insertSampleTypes(
  schema: String,
  datasetID: DatasetID,
  sampleTypes: Iterable<String>,
) =
  withPreparedBatchUpdate(SQL(schema), sampleTypes) {
    set(1, datasetID)
    set(2, it)
  }.reduceOrNull(Int::plus) ?: 0