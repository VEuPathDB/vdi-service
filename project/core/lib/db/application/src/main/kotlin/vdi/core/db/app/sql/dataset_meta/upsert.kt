package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.MaxVarchar2Length
import vdi.core.db.jdbc.set
import vdi.core.db.jdbc.setDatasetID
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
  , short_name        -- 8  short_name
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name = ?             -- 9 name
, summary = ?
, description = ?
, program_name = ?
, project_name = ?
, short_attribution = ?
, short_name = ?      -- 15 short_name
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    // insert
    set(1, datasetID)
    set(2, meta.name)
    set(3, meta.summary)
    set(4, meta.description)
    set(5, meta.programName)
    set(6, meta.projectName)
    set(7, meta.shortAttribution)
    set(8, TODO("compute short name"))

    // update
    set(9, meta.name)
    set(10, meta.summary)
    set(11, meta.description)
    set(12, meta.programName)
    set(13, meta.projectName)
    set(14, meta.shortAttribution)
    set(15, TODO("compute short_name"))
  }
}
