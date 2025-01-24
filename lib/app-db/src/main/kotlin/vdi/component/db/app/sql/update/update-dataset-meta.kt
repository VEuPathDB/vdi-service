package vdi.component.db.app.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.db.app.DatasetMetaMaxSummaryFieldLength
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
--  ${schema}.dataset_meta
  vdi_control_dev_n.dataset_meta
SET
  name = ?              -- 1
, short_name = ?
, short_attribution = ?
, category = ?
, summary = ?           -- 5
, description = ?       -- 6
WHERE
  dataset_id = ?        -- 7
"""

internal fun Connection.updateDatasetMeta(schema: String, datasetID: DatasetID, meta: VDIDatasetMeta) {
  preparedUpdate(sql(schema)) {
    setString(1, meta.name)
    setString(2, meta.shortName)
    setString(3, meta.shortAttribution)
    setString(4, meta.category)
    setString(5, meta.summary?.take(DatasetMetaMaxSummaryFieldLength))
    setString(6, meta.description)
    setDatasetID(7, datasetID)
  }
}
