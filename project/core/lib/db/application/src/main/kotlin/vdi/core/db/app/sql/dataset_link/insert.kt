package vdi.core.db.app.sql.dataset_link

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetID
import vdi.model.data.LinkedDataset

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.${Table.Links} VALUES (?, ?, ?)"

internal fun Connection.insertLinks(schema: String, datasetID: DatasetID, links: Iterable<LinkedDataset>) =
  withPreparedBatchUpdate(SQL(schema), links) {
    set(1, datasetID)
    set(2, it.datasetURI.toString())
    set(3, it.sharesRecords)
  }.reduceOrNull(Int::plus) ?: 0