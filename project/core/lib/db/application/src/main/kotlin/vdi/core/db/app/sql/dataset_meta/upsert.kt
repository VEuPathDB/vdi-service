package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import java.sql.Connection
import vdi.lib.db.app.MaxVarchar2Length
import vdi.lib.db.jdbc.setDatasetID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , short_name
  , short_attribution
  , summary
  , description
  )
VALUES
  (?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name = ?,
  short_name = ?,
  short_attribution = ?,
  summary = ?,
  description = ?
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    // insert
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(6, meta.summary.take(MaxVarchar2Length))
    setString(7, meta.description)

    // update
    setString(8, meta.name)
    setString(9, meta.shortName)
    setString(10, meta.shortAttribution)
    setString(12, meta.summary.take(MaxVarchar2Length))
    setString(13, meta.description)
  }
}
