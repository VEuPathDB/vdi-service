package vdi.lib.db.jdbc

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.ResultSet
import java.time.OffsetDateTime


fun ResultSet.reqDatasetID(column: String) = DatasetID(getString(column))

fun ResultSet.reqDatasetID(column: Int) = DatasetID(getString(column))

fun ResultSet.optDatasetID(column: String) = getString(column)?.let(::DatasetID)

fun ResultSet.optDatasetID(column: Int) = getString(column)?.let(::DatasetID)

fun ResultSet.getUserID(column: String) = UserID(getString(column))

fun ResultSet.getDateTime(column: String): OffsetDateTime = getObject(column, OffsetDateTime::class.java)

fun ResultSet.getDataType(column: String) = DataType.of(getString(column))
