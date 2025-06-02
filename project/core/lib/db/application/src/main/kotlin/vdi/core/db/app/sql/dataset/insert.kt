package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.setDeleteFlag
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset (
    dataset_id
  , owner
  , type_name
  , type_version
  , is_deleted
  , is_public
  , accessibility
  , days_for_approval
  , creation_date
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDataset(schema: String, dataset: DatasetRecord) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, dataset.datasetID)
    setUserID(2, dataset.owner)
    setDataType(3, dataset.type.name)
    setString(4, dataset.type.version)
    setDeleteFlag(5, dataset.deletionState)
    setBoolean(6, dataset.isPublic)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
    setObject(9, dataset.creationDate?.toLocalDate())
  }
}
