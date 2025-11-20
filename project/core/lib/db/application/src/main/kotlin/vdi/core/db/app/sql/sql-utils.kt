package vdi.core.db.app.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.ZoneOffset
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.db.app.TargetDBPlatform
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

internal fun PreparedStatement.setDeleteFlag(index: Int, deleteFlag: DeleteFlag) = setInt(index, deleteFlag.value)

internal fun PreparedStatement.setInstallStatus(index: Int, installStatus: InstallStatus) = setString(index, installStatus.value)

internal fun PreparedStatement.setInstallType(index: Int, installType: InstallType) = setString(index, installType.value)

internal operator fun PreparedStatement.set(index: Int, datasetID: DatasetID) = setString(index, datasetID.asString)

internal fun PreparedStatement.setUserID(index: Int, userID: UserID) = setLong(index, userID.toLong())

internal fun ResultSet.getUserID(field: String) = UserID(getLong(field))

internal fun ResultSet.getOffsetDateTime(field: String) = getTimestamp(field).toLocalDateTime().atOffset(ZoneOffset.UTC)

internal fun ResultSet.getDeleteFlag(column: String) = DeleteFlag.fromInt(getInt(column))

internal fun ResultSet.getInstallStatus(column: String) = InstallStatus.fromString(getString(column))

internal fun ResultSet.getInstallType(column: String) = InstallType.fromString(getString(column))

internal inline val Connection.platform
  get() = if (metaData.url.contains("postgres"))
    TargetDBPlatform.Postgres
  else
    TargetDBPlatform.Oracle
