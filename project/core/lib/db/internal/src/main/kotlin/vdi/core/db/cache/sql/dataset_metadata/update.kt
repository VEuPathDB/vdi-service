package vdi.core.db.cache.sql.dataset_metadata

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import java.sql.Connection
import vdi.core.db.cache.util.setDatasetVisibility
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.dataset_metadata
SET
  visibility = ?    -- 1
, name = ?
, summary = ?
, project_name = ?
, program_name = ?  -- 5
, description = ?
WHERE
  dataset_id = ?    -- 7
"""

internal fun Connection.updateDatasetMeta(
  datasetID: DatasetID,
  meta: DatasetMetadata,
) =
  withPreparedUpdate(SQL) {
    setDatasetVisibility(1, meta.visibility)
    setString(2, meta.name)
    setString(3, meta.summary)
    setString(4, meta.projectName)
    setString(5, meta.programName)
    setString(6, meta.description)
    setDatasetID(7, datasetID)
  }
