package vdi.core.db.app.sql.dataset_contact

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_contact
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetContacts(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
