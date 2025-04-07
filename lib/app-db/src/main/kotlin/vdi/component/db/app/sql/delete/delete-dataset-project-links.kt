package vdi.component.db.app.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_project
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetProjectLinks(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setDatasetID(1, datasetID) }
