package vdi.component.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.setDatasetVisibility
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_metadata (
    dataset_id        -- 1
  , visibility
  , name
  , short_name
  , short_attribution -- 5
  , category
  , summary
  , description
  , source_url        -- 9
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertDatasetMeta(datasetID: DatasetID, meta: VDIDatasetMeta) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setDatasetVisibility(2, meta.visibility)
    setString(3, meta.name)
    setString(4, meta.shortName)
    setString(5, meta.shortAttribution)
    setString(6, meta.category)
    setString(7, meta.summary)
    setString(8, meta.description)
    setString(9, meta.sourceURL)
  }
