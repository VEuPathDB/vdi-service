package vdi.lib.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.lib.db.app.model.DatasetRecord
import vdi.lib.db.app.sql.setDeleteFlag
import vdi.lib.db.jdbc.setDataType
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset (
    dataset_id        -- 1
  , owner
  , type_name
  , type_version
  , is_deleted        -- 5
  , is_public
  , accessibility
  , days_for_approval
  , creation_date     -- 9
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDataset(schema: String, dataset: DatasetRecord) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, dataset.datasetID)
    setUserID(2, dataset.owner)
    setDataType(3, dataset.typeName)
    setString(4, dataset.typeVersion)
    setDeleteFlag(5, dataset.deletionState)
    setBoolean(6, dataset.isPublic)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
    setObject(9, dataset.creationDate?.toLocalDate())
  }
}
