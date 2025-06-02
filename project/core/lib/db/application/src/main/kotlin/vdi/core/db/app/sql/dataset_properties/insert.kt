package vdi.core.db.app.sql.dataset_properties

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.json.JSON
import vdi.model.data.DatasetID
import vdi.model.data.DatasetProperties

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_properties (
    dataset_id
  , json
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetProperties(schema: String, datasetID: DatasetID, props: DatasetProperties) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, JSON.writeValueAsString(props))
  }
}
