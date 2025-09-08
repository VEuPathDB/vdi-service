package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.set
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , summary
  , description
  , program_name      -- 5  program_name
  , project_name
  , short_attribution
  , short_name
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    set(1, datasetID)
    set(2, meta.name)
    set(3, meta.summary)
    set(4, meta.description)
    set(5, meta.programName)
    set(6, meta.projectName)
    set(7, meta.shortAttribution)
    set(8, TODO("compute short name!"))
  }
}
