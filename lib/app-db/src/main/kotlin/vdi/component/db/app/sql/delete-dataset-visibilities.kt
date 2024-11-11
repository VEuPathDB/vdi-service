package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetVisibilities(schema: String, datasetID: DatasetID) =
  preparedUpdate(sql(schema)) {setDatasetID(1, datasetID) }
