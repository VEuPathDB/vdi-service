package vdi.core.db.cache.sql.dataset_projects

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
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

internal fun Connection.tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<InstallTargetID>) =
  withPreparedBatchUpdate(SQL, projects) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }.reduceOrNull(Int::plus) ?: 0
