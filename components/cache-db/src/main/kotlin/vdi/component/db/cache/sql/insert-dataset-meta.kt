package vdi.component.db.cache.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id
  , name
  , summary
  , description
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(
  datasetID: DatasetID,
  name: String,
  summary: String?,
  description: String?,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, name)
    ps.setString(3, summary)
    ps.setString(4, description)

    ps.execute()
  }
}