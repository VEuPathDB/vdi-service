package vdi.core.db.app.sql.dataset_contact

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetContact
import vdi.model.meta.DatasetID


private fun sql(schema: String) =
// language=postgresql
  """
INSERT INTO
  ${schema}.${Table.Contacts} (
    dataset_id
  , is_primary
  , first_name
  , middle_name
  , last_name     -- 5
  , email
  , affiliation
  , country
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetContacts(
  schema: String,
  datasetID: DatasetID,
  contacts: Iterable<DatasetContact>,
) =
  withPreparedBatchUpdate(sql(schema), contacts) {
    set(1, datasetID)
    set(2, it.isPrimary)
    set(3, it.firstName)
    set(4, it.middleName)
    set(5, it.lastName)
    set(6, it.email)
    set(7, it.affiliation)
    set(8, it.country)
  }.reduceOrNull(Int::plus) ?: 0

