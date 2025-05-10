package vdi.lib.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import java.sql.Connection
import vdi.lib.db.app.MaxVarchar2Length
import vdi.lib.db.jdbc.setDatasetID

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset_meta
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
  withPreparedUpdate(sql(schema)) {
    setString(1, meta.name)
    setString(2, meta.shortName)
    setString(3, meta.shortAttribution)
    setString(4, meta.category)
    setString(5, meta.summary?.take(MaxVarchar2Length))
    setString(6, meta.description)
    setDatasetID(7, datasetID)
  }
}
