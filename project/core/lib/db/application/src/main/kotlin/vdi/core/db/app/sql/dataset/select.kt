package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.usingResults
import io.foxcapades.kdbc.withPreparedStatement
import vdi.model.data.DatasetID
import vdi.model.data.DatasetVisibility
import java.sql.Connection
import java.time.LocalDateTime
import java.time.ZoneId
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.getDeleteFlag
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.jdbc.setDatasetID

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
, accessibility
, days_for_approval
, creation_date
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
          datasetID       = rs.reqDatasetID("dataset_id"),
          owner           = rs.getUserID("owner"),
          typeName        = rs.getDataType("type_name"),
          typeVersion     = rs.getString("type_version"),
          deletionState   = rs.getDeleteFlag("is_deleted"),
          isPublic        = rs.getBoolean("is_public"),
          accessibility   = rs.getString("accessibility")?.let(DatasetVisibility::fromString)
            ?: if (rs.getBoolean("is_public")) DatasetVisibility.Public else DatasetVisibility.Private,
          daysForApproval = rs.getInt("days_for_approval").let { if (rs.wasNull()) -1 else it },
          creationDate    = rs.getObject("creation_date", LocalDateTime::class.java)
            ?.atZone(ZoneId.systemDefault())
            ?.toOffsetDateTime()
        )
    }
  }
