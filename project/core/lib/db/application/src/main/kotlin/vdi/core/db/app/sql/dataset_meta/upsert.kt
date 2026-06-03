package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata

private fun sql(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.${Table.Meta} (
    dataset_id
  , name
  , summary
  , description
  , program_name      -- 5  program_name
  , project_name
  , short_attribution
  , short_name
  , disclaimer        -- 9  disclaimer
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (dataset_id) DO UPDATE SET
  name = ?              -- 10 name
, summary = ?
, description = ?
, program_name = ?
, project_name = ?
, short_attribution = ? -- 15
, short_name = ?
, disclaimer = ?        -- 17 disclaimer
"""

internal fun Connection.upsertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    // insert
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.summary)
    setString(4, meta.description)
    setString(5, meta.programName)
    setString(6, meta.projectName)
    setString(7, meta.shortAttribution)
    setString(8, meta.shortName)
    setString(9, meta.dataDisclaimer)

    // update
    setString(10, meta.name)
    setString(11, meta.summary)
    setString(12, meta.description)
    setString(13, meta.programName)
    setString(14, meta.projectName)
    setString(15, meta.shortAttribution)
    setString(16, meta.shortName)
    setString(17, meta.dataDisclaimer)
  }
}
