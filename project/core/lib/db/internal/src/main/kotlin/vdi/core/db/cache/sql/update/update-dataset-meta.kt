package vdi.core.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import java.sql.Connection
import vdi.core.db.cache.util.setDatasetVisibility
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.dataset_metadata
SET
  name = ?
, short_name = ?
, short_attribution = ?
, summary = ?
, description = ?
, visibility = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetMeta(
  datasetID: DatasetID,
  meta: DatasetMetadata,
) =
  withPreparedUpdate(SQL) {
    setString(1, meta.name)
    setString(2, meta.shortName)
    setString(3, meta.shortAttribution)
    setString(4, meta.summary)
    setString(5, meta.description)
    setDatasetVisibility(6, meta.visibility)
    setDatasetID(7, datasetID)
  }
