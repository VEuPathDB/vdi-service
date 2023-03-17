package vdi.component.db.cache.tables

import org.veupathdb.service.vdi.generated.model.DatasetImportStatus
import java.sql.Connection
import vdi.components.common.fields.DatasetID

object ImportControl {
  private const val STATUS_AWAITING_IMPORT = "awaiting-import"
  private const val STATUS_IMPORTING = "importing"
  private const val STATUS_IMPORTED = "imported"
  private const val STATUS_FAILED = "failed"

  // region Delete

  // language=postgresql
  private const val DELETE_SQL = """
    DELETE FROM
      vdi.import_control
    WHERE
      dataset_id = ?
  """

  fun delete(con: Connection, datasetID: DatasetID) {
    con.prepareStatement(DELETE_SQL).use { ps ->
      ps.setString(1, datasetID.toString())
      ps.execute()
    }
  }

  // endregion Delete

  // region Insert

  // language=postgresql
  private const val INSERT_SQL = """
    INSERT INTO
      vdi.import_control (
        dataset_id
      , status
      )
    VALUES
      (?, ?)
  """

  fun insert(con: Connection, datasetID: DatasetID, status: DatasetImportStatus) {
    con.prepareStatement(INSERT_SQL).use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, status.toDBString())
      ps.execute()
    }
  }

  // endregion Insert

  // region Select

  // language=postgresql
  private const val SELECT_SQL = """
    SELECT
      status
    FROM
      vdi.import_control
    WHERE
      dataset_id = ?
  """

  fun select(con: Connection, datasetID: DatasetID): DatasetImportStatus? =
    con.prepareStatement(SELECT_SQL).use { ps ->
      ps.setString(1, datasetID.toString())
      ps.executeQuery().use { rs ->
        if (rs.next())
          null
        else
          rs.getString(1).toImportStatus()
      }
    }

  // endregion Select

  // region Update

  // language=postgresql
  private const val UPDATE_SQL = """
    UPDATE
      vdi.import_control
    SET
      status = ?
    WHERE
      dataset_id = ?
  """

  fun update(con: Connection, datasetID: DatasetID, status: DatasetImportStatus) {
    con.prepareStatement(UPDATE_SQL).use { ps ->
      ps.setString(1, status.toDBString())
      ps.setString(2, datasetID.toString())
    }
  }

  // endregion Update

  private fun DatasetImportStatus.toDBString() =
    when (this) {
      DatasetImportStatus.AWAITINGIMPORT -> vdi.component.db.cache.tables.ImportControl.STATUS_AWAITING_IMPORT
      DatasetImportStatus.IMPORTING      -> vdi.component.db.cache.tables.ImportControl.STATUS_IMPORTING
      DatasetImportStatus.IMPORTED       -> vdi.component.db.cache.tables.ImportControl.STATUS_IMPORTED
      DatasetImportStatus.IMPORTFAILED   -> vdi.component.db.cache.tables.ImportControl.STATUS_FAILED
    }

  private fun String.toImportStatus() =
    when (this) {
      STATUS_AWAITING_IMPORT -> DatasetImportStatus.AWAITINGIMPORT
      STATUS_IMPORTING       -> DatasetImportStatus.IMPORTING
      STATUS_IMPORTED        -> DatasetImportStatus.IMPORTED
      STATUS_FAILED          -> DatasetImportStatus.IMPORTFAILED
      else                   -> throw IllegalStateException("unrecognized import status value: $this")
    }
}