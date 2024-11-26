package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetRecord
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT 
  dataset_id
, owner
, type_name
, type_version
, is_deleted
, is_public
FROM
  ${schema}.dataset
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDataset(schema: String, datasetID: DatasetID): DatasetRecord? {
  prepareStatement(sql(schema)).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return DatasetRecord(
        datasetID   = rs.getDatasetID("dataset_id"),
        owner       = rs.getUserID("owner"),
        typeName    = rs.getDataType("type_name"),
        typeVersion = rs.getString("type_version"),
        isDeleted   = rs.getDeleteFlag("is_deleted"),
        isPublic    = rs.getBoolean("is_public")
      )
    }
  }
}
