package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

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
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.summary)
    setString(4, meta.description)
    setString(5, meta.programName)
    setString(6, meta.projectName)
    setString(7, meta.shortAttribution)
    setString(8, meta.shortName)
  }
}
