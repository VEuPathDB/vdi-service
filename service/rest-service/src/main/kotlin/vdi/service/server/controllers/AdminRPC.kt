package vdi.service.server.controllers

import cleanup.InstallCleaner
import cleanup.ReinstallTarget
import kotlinx.coroutines.runBlocking
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.toUserID
import vdi.component.pruner.Pruner
import vdi.lib.install.retry.DatasetReinstaller
import vdi.lib.reconciler.Reconciler
import vdi.service.generated.model.DatasetObjectPurgeRequestBody
import vdi.service.generated.model.DatasetPostRequestBody
import vdi.service.generated.model.InstallCleanupRequestBody
import vdi.service.generated.resources.AdminRpc
import vdi.service.server.inputs.cleanup
import vdi.service.server.inputs.validate
import vdi.service.server.outputs.*
import vdi.service.server.services.admin.purgeDataset
import vdi.service.server.services.dataset.createDataset
import vdi.service.generated.resources.AdminRpc.PostAdminRpcDatasetsProxyUploadResponse as ProxyUploadResponse
import vdi.service.generated.resources.AdminRpc.PostAdminRpcDatasetsPruneResponse as DatasetPruneResponse
import vdi.service.generated.resources.AdminRpc.PostAdminRpcDatasetsReconcileResponse as ReconcileResponse
import vdi.service.generated.resources.AdminRpc.PostAdminRpcInstallsClearFailedResponse as InstallCleanupResponse

@AdminRequired
@Authenticated(adminOverride = ALLOW_ALWAYS)
class AdminRPC : AdminRpc {

  override fun postAdminRpcDatasetsProxyUpload(
    userId: Long?,
    entity: DatasetPostRequestBody?
  ): ProxyUploadResponse {
    if (userId == null)
      return BadRequestError("missing target user ID").wrap()

    with(entity ?: return BadRequestError("missing request body").wrap()) {
      cleanup()
      validate().let {
        if (it.isNotEmpty)
          return UnprocessableEntityError(it).wrap()
      }
    }

    val userID = userId.toUserID()

    with (UserProvider.getUsersById(listOf(userID.toLong()))) {
      if (isEmpty() || get(userID.toLong())?.isGuest != false)
        return ForbiddenError("target user does not exist or is a guest user").wrap()
    }

    val datasetID = runBlocking { DatasetID() }

    createDataset(userID, datasetID, entity)

    return ProxyUploadResponse.respond200WithApplicationJson(DatasetPostResponseBody(datasetID))
  }

  override fun postAdminRpcDatasetsPrune(ageCutoff: String?): DatasetPruneResponse {
    Pruner.tryPruneDatasets()
    return DatasetPruneResponse.respond204()
  }

  override fun postAdminRpcDatasetsReconcile() =
    runBlocking {
      if (Reconciler.runFull()) {
        ReconcileResponse.respond204()
      } else {
        ConflictError("reconciler is already running").wrap()
      }
    }!!

  override fun postAdminRpcInstallsClearFailed(
    skipRun: Boolean,
    entity: InstallCleanupRequestBody?
  ): InstallCleanupResponse {
    if (entity == null)
      return BadRequestError("empty request body").wrap()

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (!entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetId), it.projectId) })
    }

    if (!skipRun)
      runBlocking { DatasetReinstaller.tryRun() }

    return InstallCleanupResponse.respond204()
  }

  override fun postAdminRpcObjectStorePurgeDataset(request: DatasetObjectPurgeRequestBody) =
    purgeDataset(request)
}
