package vdi.lib.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import java.sql.Connection
import vdi.lib.db.cache.util.setDatasetVisibility
import vdi.lib.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.dataset_metadata
SET
  name = ?              -- 1
, short_name = ?
, short_attribution = ?
, category = ?
, summary = ?           -- 5
, description = ?
, visibility = ?        -- 7
WHERE
  dataset_id = ?        -- 8
"""

internal fun Connection.updateDatasetMeta(
  datasetID: DatasetID,
  meta: VDIDatasetMeta,
) =
  withPreparedUpdate(SQL) {
    setString(1, meta.name)
    setString(2, meta.shortName)
    setString(3, meta.shortAttribution)
    setString(4, meta.category)
    setString(5, meta.summary)
    setString(6, meta.description)
    setDatasetVisibility(7, meta.visibility)
    setDatasetID(8, datasetID)
  }
