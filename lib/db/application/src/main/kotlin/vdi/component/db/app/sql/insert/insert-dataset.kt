package vdi.component.db.app.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.sql.*
import vdi.lib.db.jdbc.setDataType
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID
import java.sql.Connection

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
  )
VALUES
  (?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDataset(schema: String, dataset: DatasetRecord) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, dataset.datasetID)
    setUserID(2, dataset.owner)
    setDataType(3, dataset.typeName)
    setString(4, dataset.typeVersion)
    setDeleteFlag(5, dataset.deletionState)
    setBoolean(6, dataset.isPublic)
  }
}
