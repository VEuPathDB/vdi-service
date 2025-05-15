package vdi.lib.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import java.sql.Connection
import vdi.lib.db.app.MaxVarchar2Length
import vdi.lib.db.jdbc.setDatasetID

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id        -- 1
  , name
  , short_name
  , short_attribution
  , category          -- 5
  , summary
  , description       -- 7
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name = ?,              -- 8
  short_name = ?,
  short_attribution = ?, -- 10
  category = ?,
  summary = ?,
  description = ?        -- 13
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, meta: VDIDatasetMeta) {
  withPreparedUpdate(sql(schema)) {
    // insert
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(5, meta.category)
    setString(6, meta.summary?.take(MaxVarchar2Length))
    setString(7, meta.description)

    // update
    setString(8, meta.name)
    setString(9, meta.shortName)
    setString(10, meta.shortAttribution)
    setString(11, meta.category)
    setString(12, meta.summary?.take(MaxVarchar2Length))
    setString(13, meta.description)
  }
}
