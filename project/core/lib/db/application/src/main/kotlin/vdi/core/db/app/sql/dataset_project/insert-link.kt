package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.db.app.InstallTargetRegistry
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.${Table.Projects} (
    dataset_id
  , project_id
  , project_name
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetProjectLink(schema: String, datasetID: DatasetID, installTarget: InstallTargetID) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, installTarget)
    setString(3, InstallTargetRegistry[installTarget].first().second.name)
  }
}
