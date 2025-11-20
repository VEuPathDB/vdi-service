package vdi.core.db.app.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=sql
"""
DELETE FROM
  ${schema}.${Table.SyncControl}
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteSyncControl(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
