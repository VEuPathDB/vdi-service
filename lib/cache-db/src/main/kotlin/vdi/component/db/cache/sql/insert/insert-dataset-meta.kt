package vdi.component.db.cache.sql.insert

import vdi.component.db.cache.model.DatasetMeta
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id
  , visibility
  , name
  , summary
  , description
  , source_url
  )
VALUES
  (?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetMeta(row: DatasetMeta) =
  preparedUpdate(SQL) {
    setDatasetID(1, row.datasetID)
    setString(2, row.visibility.value)
    setString(3, row.name)
    setString(4, row.summary)
    setString(5, row.description)
    setString(6, row.sourceURL)
  }
