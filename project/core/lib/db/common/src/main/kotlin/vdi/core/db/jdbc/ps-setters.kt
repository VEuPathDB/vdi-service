package vdi.core.db.jdbc

import vdi.model.meta.DataType
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import java.sql.PreparedStatement
import java.time.OffsetDateTime


/**
 * Sets the designated parameter to the given [DatasetID] value.
 *
 * @param index 1 based index of the parameter to set.
 *
 * @param datasetID Dataset ID to set to the target parameter.
 */
fun PreparedStatement.setDatasetID(index: Int, datasetID: DatasetID) =
  setString(index, datasetID.asString)

operator fun PreparedStatement.set(index: Int, datasetID: DatasetID) =
  setString(index, datasetID.asString)

/**
 * Sets the designated parameter to the given [UserID] value.
 *
 * @param index 1 based index of the parameter to set.
 *
 * @param userID User ID to set to the target parameter.
 */
fun PreparedStatement.setUserID(index: Int, userID: UserID) =
  setString(index, userID.toString())


fun PreparedStatement.setDateTime(index: Int, dateTime: OffsetDateTime) =
  setObject(index, dateTime)

fun PreparedStatement.setDataType(index: Int, dataType: DataType) =
  setString(index, dataType.toString())
