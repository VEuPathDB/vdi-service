package vdi.core.db.app.sql.dataset_organism

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
DELETE FROM
  ${schema}.dataset_organism
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetOrganisms(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
