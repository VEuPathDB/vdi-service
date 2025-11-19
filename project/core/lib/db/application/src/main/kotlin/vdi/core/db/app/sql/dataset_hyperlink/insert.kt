package vdi.core.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.set
import vdi.model.meta.DatasetHyperlink
import vdi.model.meta.DatasetID


private fun sql(schema: String) =
// language=postgresql
  """
INSERT INTO
  ${schema}.${Table.Hyperlinks} (
    dataset_id
  , url
  , description
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetHyperlinks(
  schema: String,
  datasetID: DatasetID,
  hyperlinks: Iterable<DatasetHyperlink>,
) =
  withPreparedBatchUpdate(sql(schema), hyperlinks) {
    set(1, datasetID)
    set(2, it.url.toString())
    set(3, it.description)
  }.reduceOrNull(Int::plus) ?: 0
