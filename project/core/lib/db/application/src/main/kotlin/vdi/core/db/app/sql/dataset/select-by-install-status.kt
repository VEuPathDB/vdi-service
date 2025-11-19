package vdi.core.db.app.sql.dataset

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.*
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.getDeleteFlag
import vdi.core.db.app.sql.getUserID
import vdi.core.db.app.sql.setInstallStatus
import vdi.core.db.app.sql.setInstallType
import vdi.core.db.jdbc.getDataType
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetType
import vdi.model.meta.DatasetVisibility
import vdi.model.meta.InstallTargetID

private fun sql(schema: String) =
// language=postgresql
"""
SELECT
  ds.dataset_id
, ds.owner
, ds.type_name
, ds.type_version
, ds.category
, ds.deleted_status
, accessibility
, days_for_approval
, creation_date
FROM
  ${schema}.${Table.Dataset} ds
  INNER JOIN ${schema}.${Table.InstallMessage} dsim
    ON ds.dataset_id = dsim.dataset_id
  INNER JOIN ${schema}.${Table.Projects} dsp
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
      val type = DatasetType(getDataType("type_name"), getString("type_version"))

      DatasetRecord(
        datasetID     = reqDatasetID("dataset_id"),
        owner         = getUserID("owner"),
        type          = type,
        category      = PluginRegistry.categoryOrNullFor(type) ?: getString("category"),
        deletionState = getDeleteFlag("deleted_status"),
        accessibility = getString("accessibility")?.let(DatasetVisibility::fromString)
          ?: if (getBoolean("is_public")) DatasetVisibility.Public else DatasetVisibility.Private,
        daysForApproval = getInt("days_for_approval").let { if (wasNull()) -1 else it },
        creationDate    = getOffsetDateTime("creation_date")
      )
    } }
  }
