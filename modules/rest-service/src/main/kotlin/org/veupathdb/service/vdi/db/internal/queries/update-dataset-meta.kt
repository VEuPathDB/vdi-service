package org.veupathdb.service.vdi.db.internal.queries

import java.sql.Connection
import vdi.components.common.fields.DatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.dataset_metadata
SET
  name = ?
, summary = ?
, description = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetMeta(
  datasetID: DatasetID,
  name: String,
  summary: String?,
  description: String?,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, name)
    ps.setString(2, summary)
    ps.setString(3, description)
    ps.setString(4, datasetID.toString())

    ps.execute()
  }
}