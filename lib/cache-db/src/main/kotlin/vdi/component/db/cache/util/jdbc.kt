@file:Suppress("NOTHING_TO_INLINE")

package vdi.component.db.cache.util

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.cache.model.DatasetImportStatus
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime

// region Connection

internal inline fun <T> Connection.withPreparedStatement(sql: String, fn: PreparedStatement.() -> T): T =
  prepareStatement(sql).use(fn)

internal inline fun Connection.preparedUpdate(sql: String, fn: PreparedStatement.() -> Unit) =
  prepareStatement(sql).use { fn(it); it.executeUpdate() }

// endregion Connection

// region PreparedStatement

/**
 * Sets the designated parameter to the given [DatasetID] value.
 *
 * @param index 1 based index of the parameter to set.
 *
 * @param datasetID Dataset ID to set to the target parameter.
 */
internal fun PreparedStatement.setDatasetID(index: Int, datasetID: DatasetID) =
  setString(index, datasetID.toString())

/**
 * Sets the designated parameter to the given [UserID] value.
 *
 * @param index 1 based index of the parameter to set.
 *
 * @param userID User ID to set to the target parameter.
 */
internal fun PreparedStatement.setUserID(index: Int, userID: UserID) =
  setString(index, userID.toString())

internal fun PreparedStatement.setDatasetVisibility(index: Int, visibility: VDIDatasetVisibility) =
  setString(index, visibility.value)
internal fun PreparedStatement.setDataType(index: Int, dataType: DataType) =
  setString(index, dataType.toString())
internal fun PreparedStatement.setDateTime(index: Int, dateTime: OffsetDateTime) =
  setObject(index, dateTime)
internal fun PreparedStatement.setImportStatus(index: Int, importStatus: DatasetImportStatus) =
  setString(index, importStatus.value)

internal inline fun <T> PreparedStatement.withResults(fn: ResultSet.() -> T): T =
  executeQuery().use(fn)

// endregion PreparedStatement

// region ResultSet

internal inline fun ResultSet.getStringList(column: String): List<String> =
  getArray(column).resultSet.map { it.getString(2) }

/**
 * Parses the column data in the column with the given name as an array of
 * String [ProjectID] values.
 *
 * For this method to work, the value from the query MUST be an array of
 * strings.
 *
 * @param column Name of the column to parse as an array of project IDs.
 *
 * @return A list of [ProjectID] values parsed from the target [ResultSet].
 */
internal inline fun ResultSet.getProjectIDList(column: String): List<ProjectID> =
  getStringList(column)

internal inline fun ResultSet.getDatasetID(column: String) = DatasetID(getString(column))

internal inline fun ResultSet.getUserID(column: String) = UserID(getString(column))

internal inline fun ResultSet.getDataType(column: String) = DataType.of(getString(column))

internal inline fun ResultSet.getDatasetVisibility(column: String) = VDIDatasetVisibility.fromString(getString(column))

internal inline fun ResultSet.getDateTime(column: String) = getObject(column, OffsetDateTime::class.java)

internal inline fun ResultSet.getImportStatus(column: Int) = getString(column)?.let(DatasetImportStatus::fromString)
internal inline fun ResultSet.getImportStatus(column: String) = getString(column)?.let(DatasetImportStatus::fromString)

/**
 * Parses a postgres array containing 2-value sub-arrays that contain a file
 * name and file size.
 *
 * Expected format:
 * ```json
 * [
 *   ["some_file.txt", 12345],
 *   ["other_file.tsv", 34244]
 * ]
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun ResultSet.getFileDetailList(column: String): List<VDIDatasetFileInfo> =
  arrayMap(column) {
    it.getString(2)
      .let { it.trim('(', ')') }
      .let {
        val c = it.lastIndexOf(',')
        VDIDatasetFileInfo(it.substring(0, c).trim('"'), it.substring(c + 1).toULong())
      }
  }

/**
 * Iterates over the results in the result set, calling the given function on
 * each row in the [ResultSet].
 *
 * If there are no results, the given function will not be called.
 *
 * @param fn Function that will be called on each row of the result set.
 */
internal inline fun ResultSet.forEach(fn: (rs: ResultSet) -> Unit) {
  while (next()) fn(this)
}

/**
 * Maps the rows in a target [ResultSet] to a list of values of type [T] using
 * the given mapping function ([fn]).
 *
 * If there are no results:
 * * this function will return an empty list
 * * the given mapping function will not be called
 *
 * @param T Type of values that the result rows will be mapped to.
 *
 * @param fn Mapping function that will be called on each row of the
 * [ResultSet].
 *
 * @return A list of type [T] containing the mapped values from the [ResultSet].
 */
internal inline fun <T> ResultSet.map(fn: (rs: ResultSet) -> T): List<T> =
  with(ArrayList<T>(16)) { this@map.forEach { add(fn(it)) }; this }

internal inline fun <T> ResultSet.arrayMap(column: String, fn: (rs: ResultSet) -> T): List<T> =
  ArrayList<T>(16).apply { getArray(column).resultSet.forEach { add(fn(it)) } }

// endregion ResultSet
