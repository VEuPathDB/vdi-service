package vdi.lib.db.jdbc

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.ResultSet
import java.time.OffsetDateTime


fun ResultSet.getDatasetID(column: String) = DatasetID(getString(column))

fun ResultSet.getDatasetID(column: Int) = DatasetID(getString(column))

fun ResultSet.getUserID(column: String) = UserID(getString(column))

fun ResultSet.getDateTime(column: String): OffsetDateTime = getObject(column, OffsetDateTime::class.java)

fun ResultSet.getDataType(column: String) = DataType.of(getString(column))
