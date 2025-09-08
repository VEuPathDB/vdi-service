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
UPDATE
  ${schema}.dataset_meta
SET
  name = ?
, summary = ?
, description = ?
, program_name = ?
, project_name = ?       -- 5  project_name
, short_attribution = ?
, short_name = ?
WHERE
  dataset_id = ?  -- 8
"""

internal fun Connection.updateDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    set(1, meta.name)
    set(2, meta.summary)
    set(3, meta.description)
    set(4, meta.programName)
    set(5, meta.projectName)
    set(6, meta.shortAttribution)
    set(7, TODO("compute short name!"))
    set(8, datasetID)
  }
}
