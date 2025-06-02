package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.MaxVarchar2Length
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset_meta
SET
  name = ?
, short_name = ?
, short_attribution = ?
, summary = ?
, description = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    setString(1, meta.name)
    setString(2, meta.shortName)
    setString(3, meta.shortAttribution)
    setString(5, meta.summary.take(MaxVarchar2Length))
    setString(6, meta.description)
    setDatasetID(7, datasetID)
  }
}
