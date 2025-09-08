package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.setDeleteFlag
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.dataset (
    dataset_id
  , owner
  , type_name
  , type_version
  , category
  , deleted_status
  , is_public
  , accessibility
  , days_for_approval
  , creation_date
  )
VALUES (
  ? -- 1  dataset id
, ? -- 2  owner user id
, ? -- 3  data type name
, ? -- 4  data type version
, ? -- 5  data type category
, ? -- 6  deletion status
, ? -- 7  public flag  (redundant with accessibility == public)
, ? -- 8  accessibility
, ? -- 9  days before automatic access approval to requesting users
, ? -- 10 dataset creation date
)
"""

internal fun Connection.insertDataset(schema: String, dataset: DatasetRecord) =
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, dataset.datasetID)
    setUserID(2, dataset.owner)
    setDataType(3, dataset.type.name)
    setString(4, dataset.type.version)
    setString(5, dataset.category)
    setDeleteFlag(5, dataset.deletionState)
    setBoolean(6, dataset.isPublic)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
    setObject(9, dataset.creationDate.toLocalDate())
  }
