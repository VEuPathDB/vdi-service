package vdi.core.db.app.sql.dataset_publication

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.set
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetPublication


private fun sql(schema: String) =
// language=sql
  """
INSERT INTO
  ${schema}.${Table.Publications} (
    dataset_id
  , external_id
  , type
  , citation
  , is_primary
  )
VALUES
  (?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetPublications(
  schema: String,
  datasetID: DatasetID,
  publications: Iterable<DatasetPublication>,
) =
  withPreparedBatchUpdate(sql(schema), publications) {
    set(1, datasetID)
    set(2, it.identifier)
    set(3, it.type.toString())
    set(4, it.citation)
    set(5, it.isPrimary)
  }.reduceOrNull(Int::plus) ?: 0
