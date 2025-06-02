package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDataset(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
