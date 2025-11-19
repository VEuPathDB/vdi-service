package vdi.core.db.cache.sql.dataset_metadata

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import java.sql.Connection
import vdi.core.db.cache.util.setDatasetVisibility
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id    -- 1
  , visibility
  , name
  , summary
  , project_name  -- 5
  , program_name
  , description
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setDatasetVisibility(2, meta.visibility)
    setString(3, meta.name)
    setString(4, meta.summary)
    setString(5, meta.projectName)
    setString(6, meta.programName)
    setString(7, meta.description)
  }
