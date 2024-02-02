package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.db.app.model.InstallType
import java.sql.Connection
import kotlin.math.max
import kotlin.math.min

private fun sqlStatusPrefix(schema: String) =
// language=oracle
"""
SELECT
  dataset_id
, install_type
, status
, message
FROM
  ${schema}.dataset_install_message
WHERE
  dataset_id IN (
"""

// language=oracle
private const val SQL_GET_STATUSES_SUFFIX = """
  )
"""

internal fun Connection.selectInstallStatuses(
  schema: String,
  datasetIDs: Collection<DatasetID>
): Map<DatasetID, InstallStatuses> {
  if (datasetIDs.isEmpty())
    return emptyMap()

  val result = HashMap<DatasetID, InstallStatuses>(datasetIDs.size)
  val list   = datasetIDs.toList()

  for (offset in list.indices step 1000) {
    val limit = min(1000, max(list.size - offset, 0))

    if (limit == 0)
      break

    val sb = StringBuilder(2048)
      .append(sqlStatusPrefix(schema))
      .append("    ?")

    for (i in 1 until limit)
      sb.append(", ?")

    sb.append(SQL_GET_STATUSES_SUFFIX)

    prepareStatement(sb.toString()).use { ps ->
      for (j in 0 until limit)
        ps.setString(j + 1, list[j + offset].toString())

      ps.executeQuery().use { rs ->
        while (rs.next()) {
          val datasetID = DatasetID(rs.getString("dataset_id"))
          val type      = InstallType.fromString(rs.getString("install_type"))
          val status    = InstallStatus.fromString(rs.getString("status"))
          val message   = rs.getString("message")

          if (datasetID in result) {
            when (type) {
              InstallType.Meta -> result[datasetID]!!.apply {
                meta = status
                metaMessage = message
              }

              InstallType.Data -> result[datasetID]!!.apply {
                data = status
                dataMessage = message
              }
            }
          } else {
            result[datasetID] = when (type) {
              InstallType.Meta -> InstallStatuses(meta = status, metaMessage = message)
              InstallType.Data -> InstallStatuses(data = status, dataMessage = message)
            }
          }
        }
      }
    }
  }

  return result
}
