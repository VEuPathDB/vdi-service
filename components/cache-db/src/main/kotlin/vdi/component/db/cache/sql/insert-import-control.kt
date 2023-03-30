package vdi.component.db.cache.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.component.db.cache.model.DatasetImportStatus

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.import_control (
    dataset_id
  , status
  )
VALUES
  (?, ?)
"""

fun Connection.insertImportControl(datasetID: DatasetID, status: DatasetImportStatus) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, status.value)

    ps.execute()
  }
}
