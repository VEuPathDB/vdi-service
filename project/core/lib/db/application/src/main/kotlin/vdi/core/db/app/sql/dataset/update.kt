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
UPDATE
  ${schema}.dataset
SET
  owner = ?              -- 1  dataset owner
, type_name = ?          -- 2  data type name
, type_version = ?       -- 3  data type version
, category = ?           -- 4  data type category
, deleted_status = ?     -- 5  deletion flag
, is_public = ?          -- 6  visibility == public
, accessibility = ?      -- 7  visibility
, days_for_approval = ?  -- 8  days before automatic data access request approval
WHERE
  dataset_id = ?  -- 9  dataset id
"""

internal fun Connection.updateDataset(schema: String, dataset: DatasetRecord) =
  withPreparedUpdate(sql(schema)) {
    setUserID(1, dataset.owner)
    setDataType(2, dataset.type.name)
    setString(3, dataset.type.version)
    setString(4, dataset.category)
    setDeleteFlag(5, dataset.deletionState)
    setBoolean(6, dataset.isPublic)
    setString(7, dataset.accessibility.value)
    setInt(8, dataset.daysForApproval)
    setDatasetID(9, dataset.datasetID)
  }
