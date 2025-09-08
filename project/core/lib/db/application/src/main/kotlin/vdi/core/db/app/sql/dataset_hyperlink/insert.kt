package vdi.core.db.app.sql.dataset_hyperlink

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.jdbc.set
import vdi.model.data.DatasetHyperlink
import vdi.model.data.DatasetID


private fun sql(schema: String) =
// language=postgresql
  """
INSERT INTO
  ${schema}.dataset_hyperlink (
    dataset_id
  , url
  , description
  )
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertDatasetHyperlinks(
  schema: String,
  datasetID: DatasetID,
  hyperlinks: Collection<DatasetHyperlink>,
) {
  withPreparedBatchUpdate(sql(schema), hyperlinks) {
    set(1, datasetID)
    set(2, it.url.toString())
    set(3, it.description)
  }
}
