package vdi.core.db.jdbc

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.ResultSet
import java.time.OffsetDateTime


fun ResultSet.reqDatasetID(column: String) = DatasetID(getString(column))

fun ResultSet.reqDatasetID(column: Int) = DatasetID(getString(column))

fun ResultSet.optDatasetID(column: String) = getString(column)?.let(::DatasetID)

fun ResultSet.optDatasetID(column: Int) = getString(column)?.let(::DatasetID)

fun ResultSet.getUserID(column: String) = UserID(getString(column))

fun ResultSet.getDateTime(column: String): OffsetDateTime = getObject(column, OffsetDateTime::class.java)

fun ResultSet.getDataType(column: String) = DataType.of(getString(column))
