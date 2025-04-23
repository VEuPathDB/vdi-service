package vdi.lib.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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

internal fun Connection.tryInsertDatasetProjects(datasetID: DatasetID, projects: Iterable<ProjectID>) =
  withPreparedBatchUpdate(SQL, projects) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }.reduce(Int::plus)
