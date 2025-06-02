@file:Suppress("NOTHING_TO_INLINE")

package vdi.lib.db.cache.util

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.map
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetFileInfo
import vdi.model.data.DatasetVisibility
import java.sql.PreparedStatement
import java.sql.ResultSet
import vdi.lib.db.cache.model.DatasetImportStatus

// region PreparedStatement

internal fun PreparedStatement.setDatasetVisibility(index: Int, visibility: DatasetVisibility) =
  setString(index, visibility.value)

internal fun PreparedStatement.setImportStatus(index: Int, importStatus: DatasetImportStatus) =
  setString(index, importStatus.value)

// endregion PreparedStatement

// region ResultSet

internal inline fun ResultSet.getStringList(column: String): List<String> =
  getArray(column).resultSet.map { it.getString(2) }

/**
 * Parses the column data in the column with the given name as an array of
 * String [InstallTargetID] values.
 *
 * For this method to work, the value from the query MUST be an array of
 * strings.
 *
 * @param column Name of the column to parse as an array of project IDs.
 *
 * @return A list of [InstallTargetID] values parsed from the target [ResultSet].
 */
internal inline fun ResultSet.getProjectIDList(column: String): List<InstallTargetID> =
  getStringList(column)

internal inline fun ResultSet.getDatasetVisibility(column: String) = DatasetVisibility.fromString(getString(column))

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
internal inline fun ResultSet.getFileDetailList(column: String): List<DatasetFileInfo> =
  arrayMap(column) { rs ->
    rs.getString(2)
      .trim('(', ')')
      .let {
        val c = it.lastIndexOf(',')
        DatasetFileInfo(it.substring(0, c).trim('"'), it.substring(c + 1).toULong())
      }
  }

internal inline fun <T> ResultSet.arrayMap(column: String, fn: (rs: ResultSet) -> T): List<T> =
  ArrayList<T>(16).apply { getArray(column).resultSet.forEach { add(fn(it)) } }

// endregion ResultSet
