package vdi.service.plugin.server.context

import io.ktor.server.plugins.BadRequestException
import vdi.lib.db.app.InstallTargetRegistry
import vdi.lib.db.app.TargetDatabaseDetails
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

suspend fun withDatabaseDetails(target: InstallTargetID, type: DatasetType, fn: suspend (TargetDatabaseDetails) -> Unit) =
  InstallTargetRegistry[target, type]?.also { fn(it.installDatabase) }
    ?: throw BadRequestException("unrecognized projectID value")
