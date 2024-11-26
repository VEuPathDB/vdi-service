package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_meta
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetMeta(schema: String, datasetID: DatasetID) =
  preparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
