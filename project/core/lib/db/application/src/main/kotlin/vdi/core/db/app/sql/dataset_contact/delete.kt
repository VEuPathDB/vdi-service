package vdi.core.db.app.sql.dataset_contact

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.model.data.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
DELETE FROM
  ${schema}.${Table.Contacts}
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetContacts(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
