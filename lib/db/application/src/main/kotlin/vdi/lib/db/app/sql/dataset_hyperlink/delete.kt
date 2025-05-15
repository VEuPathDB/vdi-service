package vdi.lib.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_hyperlink
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetHyperlinks(schema: String, datasetID: DatasetID) =
  withPreparedUpdate(sql(schema)) { setString(1, datasetID.toString()) }
