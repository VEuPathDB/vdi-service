package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.MaxVarchar2Length
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , short_name
  , short_attribution
  , summary
  , description
  )
VALUES
  (?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  // limit to fit into varchar(4000)
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(5, meta.summary.take(MaxVarchar2Length))
    setString(6, meta.description)
  }
}
