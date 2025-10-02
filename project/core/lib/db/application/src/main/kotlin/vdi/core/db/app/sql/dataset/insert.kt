package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.setDeleteFlag
import vdi.core.db.jdbc.setDataType
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.app.sql.setUserID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.${Table.Dataset} (
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
ON CONFLICT (dataset_id) DO UPDATE
SET
  deleted_status = ?    -- 11 deletion status
, is_public = ?         -- 12 public flag (redundant with accessibility == public)
, accessibility = ?     -- 13 accessibility
, days_for_approval = ? -- 14 days for approval
"""

internal fun Connection.upsertDataset(schema: String, dataset: DatasetRecord) =
  withPreparedUpdate(sql(schema)) {
    // insert
    setDatasetID(1, dataset.datasetID)
    setUserID(2, dataset.owner)
    setDataType(3, dataset.type.name)
    setString(4, dataset.type.version)
    setString(5, dataset.category)
    setDeleteFlag(6, dataset.deletionState)
    setBoolean(7, dataset.isPublic)
    setString(8, dataset.accessibility.value)
    setInt(9, dataset.daysForApproval)
    setObject(10, dataset.creationDate.toLocalDate())

    // update
    setDeleteFlag(11, dataset.deletionState)
    setBoolean(12, dataset.isPublic)
    setString(13, dataset.accessibility.value)
    setInt(14, dataset.daysForApproval)
  }
