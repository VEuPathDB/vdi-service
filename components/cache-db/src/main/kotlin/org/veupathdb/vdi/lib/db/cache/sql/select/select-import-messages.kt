package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  message
FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

internal fun Connection.selectImportMessages(datasetID: DatasetID): List<String> {
  val out = ArrayList<String>(2)

  prepareStatement(SQL).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.executeQuery().use { rs -> while (rs.next()) { out.add(rs.getString(1)) } }
  }

  return out
}