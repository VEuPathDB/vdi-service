package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
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
): List<DatasetRecord> {
  prepareStatement(sql(schema)).use { ps ->
    ps.setInstallType(1, installType)
    ps.setInstallStatus(2, installStatus)
    ps.setString(3, projectID)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return emptyList()

      val out = ArrayList<DatasetRecord>()

      do {
        out.add(DatasetRecord(
          datasetID   = rs.getDatasetID("dataset_id"),
          owner       = rs.getUserID("owner"),
          typeName    = rs.getDataType("type_name"),
          typeVersion = rs.getString("type_version"),
          isDeleted   = rs.getDeleteFlag("is_deleted"),
          isPublic    = rs.getBoolean("is_public"),
        ))
      } while (rs.next())

      return out
    }
  }
}
