package vdi.core.db.app.sql.dataset_source

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.usingPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.set
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetSource


private fun sql(schema: String) =
// language=sql
  """
INSERT INTO
  ${schema}.${Table.ExternalSource} (
    dataset_id
  , url
  , version
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetSources(
  schema: String,
  datasetID: DatasetID,
  sources: Iterable<DatasetSource>,
) =
  usingPreparedBatchUpdate(sql(schema), sources) { ps, source ->
    ps[1] = datasetID
    ps[2] = source.url
    ps[3] = source.version
  }
