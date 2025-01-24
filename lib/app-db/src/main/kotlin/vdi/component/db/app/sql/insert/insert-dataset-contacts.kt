package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetContact
import vdi.component.db.app.sql.preparedBatchUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_contact (
    dataset_id  -- 1
  , is_primary
  , name
  , email
  , affiliation -- 5
  , city
  , state
  , country
  , address     -- 9
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetContacts(
  schema: String,
  datasetID: DatasetID,
  contacts: Collection<VDIDatasetContact>,
) {
  preparedBatchUpdate(sql(schema), contacts) {
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
