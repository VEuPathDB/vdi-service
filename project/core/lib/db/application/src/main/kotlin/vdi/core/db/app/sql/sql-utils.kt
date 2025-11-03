package vdi.core.db.app.sql

import java.sql.PreparedStatement
import java.sql.ResultSet
import vdi.core.db.app.model.DeleteFlag
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.model.data.DatasetID

internal fun PreparedStatement.setDeleteFlag(index: Int, deleteFlag: DeleteFlag) = setInt(index, deleteFlag.value)

internal fun PreparedStatement.setInstallStatus(index: Int, installStatus: InstallStatus) = setString(index, installStatus.value)

internal fun PreparedStatement.setInstallType(index: Int, installType: InstallType) = setString(index, installType.value)

internal operator fun PreparedStatement.set(index: Int, datasetID: DatasetID) = setString(index, datasetID.asString)

internal fun ResultSet.getDeleteFlag(column: String) = DeleteFlag.fromInt(getInt(column))

internal fun ResultSet.getInstallStatus(column: String) = InstallStatus.fromString(getString(column))

internal fun ResultSet.getInstallType(column: String) = InstallType.fromString(getString(column))
