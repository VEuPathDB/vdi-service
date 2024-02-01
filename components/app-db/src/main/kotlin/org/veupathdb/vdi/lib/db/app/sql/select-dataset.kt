package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
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
FROM
  ${schema}.dataset
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDataset(schema: String, datasetID: DatasetID): DatasetRecord? {
  prepareStatement(sql(schema)).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return DatasetRecord(
        datasetID   = DatasetID(rs.getString("dataset_id")),
        owner       = UserID(rs.getLong("owner")),
        typeName    = rs.getString("type_name"),
        typeVersion = rs.getString("type_version"),
        isDeleted   = DeleteFlag.fromInt(rs.getInt("is_deleted")),
      )
    }
  }
}