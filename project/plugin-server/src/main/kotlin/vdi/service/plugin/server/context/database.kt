package vdi.service.plugin.server.context

import io.ktor.server.plugins.BadRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.db.app.InstallTarget
import vdi.db.app.InstallTargetRegistry
import vdi.db.app.TargetDatabaseDetails
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

@OptIn(ExperimentalContracts::class)
suspend fun withDatabaseDetails(target: InstallTargetID, type: DatasetType, fn: suspend (TargetDatabaseDetails) -> Unit): InstallTarget {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }
  return InstallTargetRegistry[target, type]?.also { fn(it.installDatabase) }
    ?: throw BadRequestException("unrecognized projectID value")
}
