package vdi.core.db.app.sql.dataset_publication

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_publication
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetPublications(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
