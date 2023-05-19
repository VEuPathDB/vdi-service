package org.veupathdb.vdi.lib.db.cache.util

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
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

internal inline fun <T> PreparedStatement.withResults(fn: ResultSet.() -> T): T =
  executeQuery().use(fn)

// endregion PreparedStatement

// region ResultSet

@Suppress("NOTHING_TO_INLINE")
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
@Suppress("NOTHING_TO_INLINE")
internal inline fun ResultSet.getProjectIDList(column: String): List<ProjectID> =
  getStringList(column)

@Suppress("NOTHING_TO_INLINE")
internal inline fun ResultSet.getDatasetID(column: String) = DatasetID(getString(column))

@Suppress("NOTHING_TO_INLINE")
internal inline fun ResultSet.getUserID(column: String) = UserID(getString(column))

@Suppress("NOTHING_TO_INLINE")
internal inline fun ResultSet.getDateTime(column: String) = getObject(column, OffsetDateTime::class.java)

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

// endregion ResultSet
