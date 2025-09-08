package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetProjectLinks(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
