package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.usingResults
import io.foxcapades.kdbc.withPreparedStatement
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.sql.*
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getDatasetID
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.setDatasetID
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

internal fun Connection.selectDataset(schema: String, datasetID: DatasetID): DatasetRecord? =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)

    usingResults { rs ->
      if (!rs.next())
        null
      else
        DatasetRecord(
          datasetID   = rs.getDatasetID("dataset_id"),
          owner       = rs.getUserID("owner"),
          typeName    = rs.getDataType("type_name"),
          typeVersion = rs.getString("type_version"),
          deletionState   = rs.getDeleteFlag("is_deleted"),
          isPublic    = rs.getBoolean("is_public")
        )
    }
  }
