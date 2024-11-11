package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime

internal fun PreparedStatement.setUserID(index: Int, userID: UserID) = setLong(index, userID.toLong())

internal fun PreparedStatement.setDataType(index: Int, dataType: DataType) = setString(index, dataType.toString())

internal fun PreparedStatement.setDeleteFlag(index: Int, deleteFlag: DeleteFlag) = setInt(index, deleteFlag.value)

internal fun PreparedStatement.setDatasetID(index: Int, datasetID: DatasetID) = setString(index, datasetID.toString())

internal fun PreparedStatement.setDateTime(index: Int, dateTime: OffsetDateTime) = setObject(index, dateTime)

internal fun PreparedStatement.setInstallStatus(index: Int, installStatus: InstallStatus) = setString(index, installStatus.value)

internal fun PreparedStatement.setInstallType(index: Int, installType: InstallType) = setString(index, installType.value)

internal inline fun Connection.preparedUpdate(sql: String, fn: PreparedStatement.() -> Unit): Int =
  prepareStatement(sql).use {
    it.fn()
    it.executeUpdate()
  }

internal fun ResultSet.getDatasetID(column: String) = DatasetID(getString(column))
internal fun ResultSet.getDataType(column: String) = DataType.of(getString(column))
internal fun ResultSet.getDateTime(column: String) = getObject(column, OffsetDateTime::class.java)
internal fun ResultSet.getDeleteFlag(column: String) = DeleteFlag.fromInt(getInt(column))
internal fun ResultSet.getInstallStatus(column: String) = InstallStatus.fromString(getString(column))
internal fun ResultSet.getInstallType(column: String) = InstallType.fromString(getString(column))
internal fun ResultSet.getUserID(column: String) = UserID(getLong(column))
