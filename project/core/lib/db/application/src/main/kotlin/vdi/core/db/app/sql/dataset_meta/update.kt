package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata

private fun sql(schema: String) =
// language=sql
"""
UPDATE
  ${schema}.${Table.Meta}
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
    setString(1, meta.name)
    setString(2, meta.summary)
    setString(3, meta.description)
    setString(4, meta.programName)
    setString(5, meta.projectName)
    setString(6, meta.shortAttribution)
    setString(7, meta.shortName)
    setDatasetID(8, datasetID)
  }
}
