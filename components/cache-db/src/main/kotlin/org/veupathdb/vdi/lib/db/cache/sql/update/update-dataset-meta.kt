package org.veupathdb.vdi.lib.db.cache.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
UPDATE
  vdi.dataset_metadata
SET
  name = ?
, summary = ?
, description = ?
, visibility = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetMeta(
  datasetID: DatasetID,
  visibility: VDIDatasetVisibility,
  name: String,
  summary: String?,
  description: String?
) =
  preparedUpdate(SQL) {
    setString(1, name)
    setString(2, summary)
    setString(3, description)
    setString(4, visibility.value)
    setDatasetID(5, datasetID)
  }
