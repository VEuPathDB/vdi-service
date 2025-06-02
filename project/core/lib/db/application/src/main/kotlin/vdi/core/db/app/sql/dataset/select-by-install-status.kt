package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetVisibility
import java.sql.Connection
import java.time.LocalDateTime
import java.time.ZoneId
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.getDeleteFlag
import vdi.core.db.app.sql.setInstallStatus
import vdi.core.db.app.sql.setInstallType
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  ds.dataset_id
, ds.owner
, ds.type_name
, ds.type_version
, ds.is_deleted
, ds.is_public
, accessibility
, days_for_approval
, creation_date
FROM
  ${schema}.dataset ds
  INNER JOIN ${schema}.dataset_install_message dsim
    ON ds.dataset_id = dsim.dataset_id
  INNER JOIN ${schema}.dataset_project dsp
    ON ds.dataset_id = dsp.dataset_id
WHERE
  dsim.install_type = ?
  AND dsim.status = ?
  AND dsp.project_id = ?
"""

internal fun Connection.selectDatasetsByInstallStatus(
  schema: String,
  installType: InstallType,
  installStatus: InstallStatus,
  installTarget: InstallTargetID,
): List<DatasetRecord> =
  withPreparedStatement(sql(schema)) {
    setInstallType(1, installType)
    setInstallStatus(2, installStatus)
    setString(3, installTarget)

    withResults { map {
      DatasetRecord(
        datasetID     = reqDatasetID("dataset_id"),
        owner         = getUserID("owner"),
        typeName      = getDataType("type_name"),
        typeVersion   = getString("type_version"),
        deletionState = getDeleteFlag("is_deleted"),
        isPublic      = getBoolean("is_public"),
        accessibility = getString("accessibility")?.let(DatasetVisibility::fromString)
          ?: if (getBoolean("is_public")) DatasetVisibility.Public else DatasetVisibility.Private,
        daysForApproval = getInt("days_for_approval").let { if (wasNull()) -1 else it },
        creationDate    = getObject("creation_date", LocalDateTime::class.java)
          ?.atZone(ZoneId.systemDefault())
          ?.toOffsetDateTime()
      )
    } }
  }
