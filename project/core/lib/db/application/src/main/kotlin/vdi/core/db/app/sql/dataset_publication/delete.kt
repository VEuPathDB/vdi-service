package vdi.core.db.app.sql.dataset_publication

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=sql
"""
DELETE FROM
  ${schema}.${Table.Publications}
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetPublications(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
