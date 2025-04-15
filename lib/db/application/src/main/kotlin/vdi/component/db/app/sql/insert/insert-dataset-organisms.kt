package vdi.component.db.app.sql.insert

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_organism (
    dataset_id
  , organism_abbrev
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetOrganisms(
  schema: String,
  datasetID: DatasetID,
  organisms: Iterable<String>,
) {
  withPreparedBatchUpdate(sql(schema), organisms) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }
}
