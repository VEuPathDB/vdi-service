package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
DELETE FROM
  ${schema}.dataset_meta
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetMeta(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
