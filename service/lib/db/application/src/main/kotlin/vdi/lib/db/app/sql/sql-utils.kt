package vdi.lib.db.app.sql

import java.sql.PreparedStatement
import java.sql.ResultSet
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.app.model.InstallStatus
import vdi.lib.db.app.model.InstallType

internal fun PreparedStatement.setDeleteFlag(index: Int, deleteFlag: DeleteFlag) = setInt(index, deleteFlag.value)

internal fun PreparedStatement.setInstallStatus(index: Int, installStatus: InstallStatus) = setString(index, installStatus.value)

internal fun PreparedStatement.setInstallType(index: Int, installType: InstallType) = setString(index, installType.value)

internal fun ResultSet.getDeleteFlag(column: String) = DeleteFlag.fromInt(getInt(column))

internal fun ResultSet.getInstallStatus(column: String) = InstallStatus.fromString(getString(column))

internal fun ResultSet.getInstallType(column: String) = InstallType.fromString(getString(column))
