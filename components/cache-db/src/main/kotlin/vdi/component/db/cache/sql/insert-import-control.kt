package vdi.component.db.cache.sql

import org.veupathdb.service.vdi.generated.model.DatasetImportStatus
import java.sql.Connection
import vdi.components.common.fields.DatasetID

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
    ps.setString(2, status.toDBEnum())

    ps.execute()
  }
}

private fun DatasetImportStatus.toDBEnum() =
  when (this) {
    DatasetImportStatus.AWAITINGIMPORT -> "awaiting-import"
    DatasetImportStatus.IMPORTING      -> "importing"
    DatasetImportStatus.IMPORTED       -> "imported"
    DatasetImportStatus.IMPORTFAILED   -> "import-failed"
  }