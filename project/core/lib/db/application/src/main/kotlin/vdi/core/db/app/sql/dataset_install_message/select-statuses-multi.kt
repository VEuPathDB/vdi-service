package vdi.core.db.app.sql.dataset_install_message

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import kotlin.math.max
import kotlin.math.min
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.getInstallStatus
import vdi.core.db.app.sql.getInstallType
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID

private fun sqlStatusPrefix(schema: String) =
// language=postgresql
"""
SELECT
  dataset_id
, install_type
, status
, message
FROM
  ${schema}.${Table.InstallMessage}
WHERE
  dataset_id IN (
"""

// language=postgresql
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

    withPreparedStatement(sb.toString()) {
      for (j in 0 until limit)
        setDatasetID(j + 1, list[j + offset])

      withResults { forEach {
        val datasetID = reqDatasetID("dataset_id")
        val type      = getInstallType("install_type")
        val status    = getInstallStatus("status")
        val message   = getString("message")

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
      } }
    }
  }

  return result
}
