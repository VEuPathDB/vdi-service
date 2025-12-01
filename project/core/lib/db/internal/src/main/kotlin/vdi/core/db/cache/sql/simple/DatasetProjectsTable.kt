package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedBatchUpdate
import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

internal object DatasetProjectsTable {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.dataset_projects
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }

  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.dataset_projects (
    dataset_id
  , project_id
  )
VALUES
  (?, ?)
ON CONFLICT (dataset_id, project_id)
  DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, projects: Iterable<InstallTargetID>) =
    con.withPreparedBatchUpdate(InsertSQL, projects) {
      setDatasetID(1, datasetID)
      setString(2, it)
    }.reduceOrNull(Int::plus) ?: 0
}