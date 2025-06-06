package vdi.core.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.data.DatasetHyperlink
import vdi.model.data.DatasetID


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
  hyperlinks: Collection<DatasetHyperlink>,
) {
  withPreparedBatchUpdate(sql(schema), hyperlinks) {
    setDatasetID(1, datasetID)
    setString(2, it.url.toString())
    setString(3, it.text)
    setString(4, it.description)
    setBoolean(5, it.isPublication)
  }
}
