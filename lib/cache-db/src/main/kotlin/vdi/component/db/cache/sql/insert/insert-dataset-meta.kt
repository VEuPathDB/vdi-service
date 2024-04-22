package vdi.component.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id
  , visibility
  , name
  , summary
  , description
  , source_url
  )
VALUES
  (?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetMeta(
  datasetID:   DatasetID,
  visibility:  VDIDatasetVisibility,
  name:        String,
  summary:     String?,
  description: String?,
  sourceURL:   String?,
) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, visibility.value)
    setString(3, name)
    setString(4, summary)
    setString(5, description)
    setString(6, sourceURL)
  }
