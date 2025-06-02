package vdi.core.db.app.sql.dataset_meta

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetMetadata
import java.sql.Connection
import vdi.lib.db.app.MaxVarchar2Length
import vdi.lib.db.jdbc.setDatasetID

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
  (?, ?, ?, , ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, meta: DatasetMetadata) {
  // limit to fit into varchar(4000)
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(6, meta.summary.take(MaxVarchar2Length))
    setString(7, meta.description)
  }
}
