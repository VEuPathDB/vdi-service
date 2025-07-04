package vdi.core.db.cache.sql.dataset_metadata

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import java.sql.Connection
import vdi.core.db.cache.util.setDatasetVisibility
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id
  , visibility
  , name
  , short_name
  , short_attribution
  , summary
  , description
  , source_url
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setDatasetVisibility(2, meta.visibility)
    setString(3, meta.name)
    setString(4, meta.shortName)
    setString(5, meta.shortAttribution)
    setString(6, meta.summary)
    setString(7, meta.description)
    setString(8, meta.sourceURL?.toString())
  }
