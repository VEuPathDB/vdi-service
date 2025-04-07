package vdi.component.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteInstallFiles(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }
