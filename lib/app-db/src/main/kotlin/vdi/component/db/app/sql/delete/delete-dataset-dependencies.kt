package vdi.component.db.app.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.sql.preparedUpdate
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_dependency
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetDependencies(schema: String, datasetID: DatasetID) =
  preparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
