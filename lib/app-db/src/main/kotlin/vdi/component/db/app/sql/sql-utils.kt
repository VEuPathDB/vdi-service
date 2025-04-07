package vdi.component.db.app.sql

import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.app.model.InstallStatus
import vdi.component.db.app.model.InstallType
import java.sql.PreparedStatement
import java.sql.ResultSet

internal fun PreparedStatement.setDeleteFlag(index: Int, deleteFlag: DeleteFlag) = setInt(index, deleteFlag.value)

internal fun PreparedStatement.setInstallStatus(index: Int, installStatus: InstallStatus) = setString(index, installStatus.value)

internal fun PreparedStatement.setInstallType(index: Int, installType: InstallType) = setString(index, installType.value)

internal fun ResultSet.getDeleteFlag(column: String) = DeleteFlag.fromInt(getInt(column))

internal fun ResultSet.getInstallStatus(column: String) = InstallStatus.fromString(getString(column))

internal fun ResultSet.getInstallType(column: String) = InstallType.fromString(getString(column))
