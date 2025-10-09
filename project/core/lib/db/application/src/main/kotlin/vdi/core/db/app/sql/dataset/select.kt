package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.usingResults
import io.foxcapades.kdbc.withPreparedStatement
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.getDeleteFlag
import vdi.core.db.app.sql.getOffsetDateTime
import vdi.core.db.jdbc.getDataType
import vdi.core.db.app.sql.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.DatasetVisibility

private fun sql(schema: String) =
// language=postgresql
"""
SELECT
  dataset_id
, owner
, type_name
, type_version
, category
, deleted_status
-- is_public is redundant
, accessibility
, days_for_approval
, creation_date
FROM
  ${schema}.${Table.Dataset}
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDataset(schema: String, datasetID: DatasetID): DatasetRecord? =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)

    usingResults { rs ->
      if (!rs.next())
        return@usingResults null

      val type = DatasetType(rs.getDataType("type_name"), rs.getString("type_version"))

      DatasetRecord(
        datasetID       = rs.reqDatasetID("dataset_id"),
        owner           = rs.getUserID("owner"),
        type            = type,
        category        = PluginRegistry.categoryOrNullFor(type) ?: rs.getString("category"),
        deletionState   = rs.getDeleteFlag("deleted_status"),
        accessibility   = rs.getString("accessibility")?.let(DatasetVisibility::fromString)
          ?: if (rs.getBoolean("is_public")) DatasetVisibility.Public else DatasetVisibility.Private,
        daysForApproval = rs.getInt("days_for_approval").let { if (rs.wasNull()) -1 else it },
        creationDate    = rs.getOffsetDateTime("creation_date")
      )
    }
  }
