package vdi.core.db.app.sql.dataset_contact

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetContact
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_contact (
    dataset_id
  , is_primary
  , name
  , email
  , affiliation
  , city
  , state
  , country
  , address
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetContacts(
  schema: String,
  datasetID: DatasetID,
  contacts: Collection<DatasetContact>,
) {
  withPreparedBatchUpdate(sql(schema), contacts) {
    setDatasetID(1, datasetID)
    setBoolean(2, it.isPrimary)
    setString(3, it.name)
    setString(4, it.email)
    setString(5, it.affiliation)
    setString(6, it.city)
    setString(7, it.state)
    setString(8, it.country)
    setString(9, it.address)
  }
}
