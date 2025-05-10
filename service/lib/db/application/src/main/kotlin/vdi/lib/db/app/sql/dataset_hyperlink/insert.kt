package vdi.lib.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID


private fun sql(schema: String) =
// language=oracle
  """
INSERT INTO
  ${schema}.dataset_hyperlink (
    dataset_id
  , url
  , text
  , description
  , is_publication
  )
VALUES
  (?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetHyperlinks(
  schema: String,
  datasetID: DatasetID,
  hyperlinks: Collection<VDIDatasetHyperlink>,
) {
  withPreparedBatchUpdate(sql(schema), hyperlinks) {
    setDatasetID(1, datasetID)
    setString(2, it.url)
    setString(3, it.text)
    setString(4, it.description)
    setBoolean(5, it.isPublication)
  }
}
