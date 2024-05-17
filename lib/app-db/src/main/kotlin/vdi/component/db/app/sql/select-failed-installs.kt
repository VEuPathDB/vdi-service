package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.model.*
import java.sql.Connection
import java.time.OffsetDateTime

private fun sql(schema: String) =
// language=oracle
"""
SELECT 
  ds.dataset_id
, ds.owner
, ds.type_name
, ds.type_version
, dsim.install_type
, dsim.install_message
, dsim.updated
FROM
  ${schema}.dataset ds
  INNER JOIN ${schema}.dataset_install_message dsim
    ON ds.dataset_id = dsim.dataset_id
  INNER JOIN ${schema}.dataset_project dsp
    ON ds.dataset_id = dsp.dataset_id
WHERE
  ds.is_deleted = 0
  AND dsim.status = ?
  AND dsp.project_id = ?
"""

internal fun Connection.selectFailedInstalls(schema: String, projectID: ProjectID): List<FailedInstallRecord> {
  prepareStatement(sql(schema)).use { ps ->
    ps.setString(1, InstallStatus.FailedInstallation.value)
    ps.setString(2, projectID)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return emptyList()

      val out = ArrayList<FailedInstallRecord>()

      do {
        out.add(FailedInstallRecord(
          datasetID      = DatasetID(rs.getString("dataset_id")),
          ownerID        = UserID(rs.getLong("owner")),
          typeName       = rs.getString("type_name"),
          typeVersion    = rs.getString("type_version"),
          installType    = InstallType.fromString(rs.getString("install_type")),
          installMessage = rs.getString("install_message"),
          updated        = rs.getObject("updated", OffsetDateTime::class.java),
        ))
      } while (rs.next())

      return out
    }
  }
}