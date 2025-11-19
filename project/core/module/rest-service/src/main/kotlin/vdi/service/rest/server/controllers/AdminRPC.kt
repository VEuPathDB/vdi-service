package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import kotlinx.coroutines.runBlocking
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated.AdminOverrideOption.ALLOW_ALWAYS
import vdi.core.install.cleanup.InstallCleaner
import vdi.core.install.cleanup.ReinstallTarget
import vdi.core.install.retry.DatasetReinstaller
import vdi.core.pruner.Pruner
import vdi.core.reconciler.Reconciler
import vdi.model.meta.DatasetID
import vdi.model.meta.toUserID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.DatasetObjectPurgeRequestBody
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody
import vdi.service.rest.generated.model.InstallCleanupRequestBody
import vdi.service.rest.generated.resources.AdminRpc
import vdi.service.rest.server.inputs.cleanup
import vdi.service.rest.server.inputs.validate
import vdi.service.rest.server.outputs.*
import vdi.service.rest.server.services.admin.rpc.purgeDataset
import vdi.service.rest.server.services.dataset.createDataset
import vdi.service.rest.util.ShortID
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcDatasetsProxyUploadResponse as ProxyUploadResponse
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcDatasetsPruneResponse as DatasetPruneResponse
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcDatasetsReconcileResponse as ReconcileResponse
import vdi.service.rest.generated.resources.AdminRpc.PostAdminRpcInstallsClearFailedResponse as InstallCleanupResponse

@AdminRequired
@Authenticated(adminOverride = ALLOW_ALWAYS)
class AdminRPC(
  @Context request: ContainerRequest,
  @param:Context val uploadConfig: UploadConfig,
): AdminRpc, ControllerBase(request) {

  override fun postAdminRpcDatasetsProxyUpload(
    userId: Long?,
    entity: DatasetProxyPostRequestBody?
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

    userID = userId.toUserID()

    with (UserProvider.getUsersById(listOf(userID.toLong()))) {
      if (isEmpty() || get(userID.toLong())?.isGuest != false)
        return ForbiddenError("target user does not exist or is a guest user").wrap()
    }

    val datasetID = runBlocking { DatasetID(ShortID.generate()) }

    createDataset(datasetID, entity, uploadConfig)

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
    skipRun: Boolean?,
    entity: InstallCleanupRequestBody?
  ): InstallCleanupResponse {
    if (entity == null)
      return BadRequestError("empty request body").wrap()

    if (entity.all == true) {
      InstallCleaner.cleanAll()
    } else if (!entity.targets.isNullOrEmpty()) {
      InstallCleaner.cleanTargets(entity.targets.map { ReinstallTarget(DatasetID(it.datasetId), it.installTarget) })
    }

    if (skipRun != true)
      runBlocking { DatasetReinstaller.tryRun() }

    return InstallCleanupResponse.respond204()
  }

  override fun postAdminRpcObjectStorePurgeDataset(request: DatasetObjectPurgeRequestBody) =
    purgeDataset(request)
}
