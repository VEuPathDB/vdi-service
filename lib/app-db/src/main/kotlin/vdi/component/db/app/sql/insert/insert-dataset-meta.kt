package vdi.component.db.app.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.component.db.app.DatasetMetaMaxSummaryFieldLength
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_meta (
    dataset_id
  , name
  , short_name
  , short_attribution
  , category
  , summary
  , description
  )
VALUES
  (?, ?, ?, ?, ?, ?, ?)
"""

internal fun Connection.insertDatasetMeta(schema: String, datasetID: DatasetID, meta: VDIDatasetMeta) {
  // limit to fit into varchar(4000)
  preparedUpdate(sql(schema)) {
    setDatasetID(1, datasetID)
    setString(2, meta.name)
    setString(3, meta.shortName)
    setString(4, meta.shortAttribution)
    setString(5, meta.category)
    setString(6, meta.summary?.take(DatasetMetaMaxSummaryFieldLength))
    setString(7, meta.description)
  }
}
