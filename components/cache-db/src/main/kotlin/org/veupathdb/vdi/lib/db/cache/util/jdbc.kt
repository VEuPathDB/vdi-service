package org.veupathdb.vdi.lib.db.cache.util

import org.veupathdb.vdi.lib.common.field.ProjectID
import java.sql.Array
import java.sql.ResultSet

internal fun Array.gatherProjectIDs(): List<ProjectID> =
  resultSet.map { it.getString(2) }

internal inline fun ResultSet.forEach(fn: (rs: ResultSet) -> Unit) {
  while (next()) fn(this)
}

internal inline fun <T> ResultSet.map(fn: (rs: ResultSet) -> T): List<T> =
  with(ArrayList<T>(16)) { this@map.forEach { add(fn(it)) }; this }