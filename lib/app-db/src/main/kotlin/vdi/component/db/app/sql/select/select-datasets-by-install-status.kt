package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import vdi.component.db.app.sql.getDeleteFlag
import vdi.component.db.app.sql.setInstallStatus
import vdi.component.db.app.sql.setInstallType
import vdi.component.db.jdbc.getDataType
import vdi.component.db.jdbc.getDatasetID
import vdi.component.db.jdbc.getUserID
import java.sql.Connection

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
  projectID: ProjectID,
): List<DatasetRecord> =
  withPreparedStatement(sql(schema)) {
    setInstallType(1, installType)
    setInstallStatus(2, installStatus)
    setString(3, projectID)

    withResults { map {
      DatasetRecord(
        datasetID   = getDatasetID("dataset_id"),
        owner       = getUserID("owner"),
        typeName    = getDataType("type_name"),
        typeVersion = getString("type_version"),
        isDeleted   = getDeleteFlag("is_deleted"),
        isPublic    = getBoolean("is_public"),
      )
    } }
  }
