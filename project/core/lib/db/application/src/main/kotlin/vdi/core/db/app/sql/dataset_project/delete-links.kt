package vdi.core.db.app.sql.dataset_project

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=sql
"""
DELETE FROM
  ${schema}.${Table.Projects}
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetProjectLinks(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
