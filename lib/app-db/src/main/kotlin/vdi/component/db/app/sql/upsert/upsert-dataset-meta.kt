package vdi.component.db.app.sql.upsert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.db.app.DatasetMetaMaxSummaryFieldLength
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection

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
  preparedUpdate(sql(schema)) {
    // insert
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(5, meta.category)
    setString(6, meta.summary?.take(DatasetMetaMaxSummaryFieldLength))
    setString(7, meta.description)

    // update
    setString(8, meta.name)
    setString(9, meta.shortName)
    setString(10, meta.shortAttribution)
    setString(11, meta.category)
    setString(12, meta.summary?.take(DatasetMetaMaxSummaryFieldLength))
    setString(13, meta.description)
  }
}
