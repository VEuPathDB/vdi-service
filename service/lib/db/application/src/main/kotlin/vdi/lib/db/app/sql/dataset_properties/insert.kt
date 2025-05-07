package vdi.lib.db.app.sql.dataset_properties

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetProperties
import org.veupathdb.vdi.lib.json.JSON
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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

internal fun Connection.insertDatasetProperties(schema: String, datasetID: DatasetID, props: VDIDatasetProperties) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, JSON.writeValueAsString(props))
  }
}
