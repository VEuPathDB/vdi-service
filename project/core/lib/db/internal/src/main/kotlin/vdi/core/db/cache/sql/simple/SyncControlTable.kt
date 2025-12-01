package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withPreparedUpdate
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.core.db.jdbc.getDateTime
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.core.db.model.SyncControlRecord
import vdi.model.meta.DatasetID

internal object SyncControlTable {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.sync_control
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }

  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.sync_control (
    dataset_id
  , shares_update_time
  , data_update_time
  , meta_update_time
  )
VALUES
  (?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(record: SyncControlRecord) =
    con.withPreparedUpdate(InsertSQL) {
      setDatasetID(1, record.datasetID)
      setDateTime(2, record.sharesUpdated)
      setDateTime(3, record.dataUpdated)
      setDateTime(4, record.metaUpdated)
    }

  // language=postgresql
  private const val SelectSQL = """
SELECT
  shares_update_time
, data_update_time
, meta_update_time
FROM
  vdi.sync_control
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID) =
    con.withPreparedStatement(SelectSQL) {
      setDatasetID(1, datasetID)
      withResults {
        if (!next())
          null
        else
          SyncControlRecord(
            datasetID      = datasetID,
            sharesUpdated  = getDateTime("shares_update_time"),
            dataUpdated    = getDateTime("data_update_time"),
            metaUpdated    = getDateTime("meta_update_time"),
          )
      }
    }

  // language=postgresql
  private const val UpdateDataSQL = """
UPDATE
  vdi.sync_control
SET
  data_update_time = ?
WHERE
  dataset_id = ?
  AND data_update_time < ?
"""

  /**
   * Updates the sync control record for a target dataset setting the data update
   * time to the given new value only if the new value is more recent than the
   * value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the data field in the target sync control
   * record.
   */
  context(con: Connection)
  internal fun updateDataTime(datasetID: DatasetID, timestamp: OffsetDateTime) =
    con.withPreparedUpdate(UpdateDataSQL) {
      setDateTime(1, timestamp)
      setDatasetID(2, datasetID)
      setDateTime(3, timestamp)
    }

  // language=postgresql
  private const val UpdateMetaSQL = """
UPDATE
  vdi.sync_control
SET
  meta_update_time = ?
WHERE
  dataset_id = ?
  AND meta_update_time < ?
"""

  /**
   * Updates the sync control record for a target dataset setting the meta update
   * time to the given new value only if the new value is more recent than the
   * value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the meta field in the target sync control
   * record.
   */
  context(con: Connection)
  internal fun updateMetaTime(datasetID: DatasetID, timestamp: OffsetDateTime) =
    con.withPreparedUpdate(UpdateMetaSQL) {
      setDateTime(1, timestamp)
      setDatasetID(2, datasetID)
      setDateTime(3, timestamp)
    }

  // language=postgresql
  private const val UpdateShareSQL = """
UPDATE
  vdi.sync_control
SET
  shares_update_time = ?
WHERE
  dataset_id = ?
  AND shares_update_time < ?
"""

  /**
   * Updates the sync control record for a target dataset setting the share update
   * time to the given new value only if the new value is more recent than the
   * value presently in the database.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be updated.
   *
   * @param timestamp New timestamp for the share field in the target sync control
   * record.
   */
  context(con: Connection)
  internal fun updateShareTime(datasetID: DatasetID, timestamp: OffsetDateTime) =
    con.withPreparedUpdate(UpdateShareSQL) {
      setDateTime(1, timestamp)
      setDatasetID(2, datasetID)
      setDateTime(3, timestamp)
    }

}