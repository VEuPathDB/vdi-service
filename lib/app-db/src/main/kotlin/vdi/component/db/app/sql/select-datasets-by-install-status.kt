package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.model.DatasetRecord
import vdi.component.db.app.model.DeleteFlag
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
    ps.setString(1, installType.value)
    ps.setString(2, installStatus.value)
    ps.setString(3, projectID)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return emptyList()

      val out = ArrayList<DatasetRecord>()

      do {
        out.add(DatasetRecord(
          datasetID   = DatasetID(rs.getString("dataset_id")),
          owner       = UserID(rs.getLong("owner")),
          typeName    = rs.getString("type_name"),
          typeVersion = rs.getString("type_version"),
          isDeleted   = DeleteFlag.fromInt(rs.getInt("is_deleted")),
        ))
      } while (rs.next())

      return out
    }
  }
}