package vdi.component.db.app.sql.insert

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetHyperlink
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection


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
