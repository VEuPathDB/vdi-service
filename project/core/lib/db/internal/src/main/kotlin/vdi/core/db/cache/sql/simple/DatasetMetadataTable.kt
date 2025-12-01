package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.cache.util.setDatasetVisibility
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata

internal object DatasetMetadataTable {
  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.dataset_metadata
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }


  // language=postgresql
  private const val InsertSQL = """
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

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, meta: DatasetMetadata) =
    con.withPreparedUpdate(InsertSQL) {
      setDatasetID(1, datasetID)
      setDatasetVisibility(2, meta.visibility)
      setString(3, meta.name)
      setString(4, meta.summary)
      setString(5, meta.projectName)
      setString(6, meta.programName)
      setString(7, meta.description)
    }


  // language=postgresql
  private const val UpdateSQL = """
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

  context(con: Connection)
  internal fun update(datasetID: DatasetID, meta: DatasetMetadata) =
    con.withPreparedUpdate(UpdateSQL) {
      setDatasetVisibility(1, meta.visibility)
      setString(2, meta.name)
      setString(3, meta.summary)
      setString(4, meta.projectName)
      setString(5, meta.programName)
      setString(6, meta.description)
      setDatasetID(7, datasetID)
    }
}