package vdi.service.rest.server.controllers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.lib.container.jaxrs.errors.UnprocessableEntityException;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired;
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import vdi.core.install.cleanup.InstallCleaner;
import vdi.core.install.cleanup.ReinstallTarget;
import vdi.core.install.retry.DatasetReinstaller;
import vdi.core.pruner.Pruner;
import vdi.core.reconciler.Reconciler;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.resources.AdminRpc;
import vdi.service.rest.s3.DatasetStore;
import vdi.service.rest.server.inputs.ProxyPostRequestBody;
import vdi.service.rest.server.services.dataset.DatasetPostService;
import vdi.service.rest.util.ShortID;

import java.util.List;

@AdminRequired
@Authenticated(adminOverride = Authenticated.AdminOverrideOption.ALLOW_ALWAYS)
public class AdminRpcController extends ControllerBase implements AdminRpc {
  private final UploadConfig uploadConfig;

  public AdminRpcController(
    @Context ContainerRequest request,
    @Context UploadConfig uploadConfig
  ) {
    super(request);
    this.uploadConfig = uploadConfig;
  }

  @Override
  public PostAdminRpcDatasetsProxyUploadResponse postAdminRpcDatasetsProxyUpload(
    Long userId,
    DatasetProxyPostRequestBody entity
  ) {
    if (userId == null)
      throw new BadRequestException("missing target user id");
    if (entity == null)
      throw new BadRequestException("missing request body");

    ProxyPostRequestBody.cleanup(entity);

    var errors = ProxyPostRequestBody.validate(entity);
    if (errors.isNotEmpty())
      throw new UnprocessableEntityException(errors.getGeneral(), errors.getByKey());

    var usersById = UserProvider.getUsersById(List.of(userId));

    if (usersById.isEmpty() || !usersById.containsKey(userId) || usersById.get(userId).isGuest())
      throw new ForbiddenException("target user does not exist or is a guest user");

    var datasetId = ShortID.generate();

    DatasetPostService.createDataset(this, datasetId, entity, uploadConfig);

    return PostAdminRpcDatasetsProxyUploadResponse.respond200WithApplicationJson(
      new DatasetPostResponseBodyImpl() {{ setDatasetId(datasetId); }}
    );
  }

  @Override
  public PostAdminRpcDatasetsPruneResponse postAdminRpcDatasetsPrune(String ageCutoff) {
    Pruner.INSTANCE.tryPruneDatasets();
    return PostAdminRpcDatasetsPruneResponse.respond204();
  }

  @Override
  public PostAdminRpcDatasetsReconcileResponse postAdminRpcDatasetsReconcile() {
    if (Reconciler.runFullReconciliation())
      return PostAdminRpcDatasetsReconcileResponse.respond204();

    return PostAdminRpcDatasetsReconcileResponse.respond409WithApplicationJson(
      new ConflictErrorImpl() {{ setMessage("reconciler is already running"); }}
    );
  }

  @Override
  public PostAdminRpcInstallsClearFailedResponse postAdminRpcInstallsClearFailed(
    Boolean skipRun,
    InstallCleanupRequestBody entity
  ) {
    if (entity == null)
      throw new BadRequestException("empty request body");

    if (entity.getAll() == true) // getAll is nullable
      InstallCleaner.cleanAll();
    else if (entity.getTargets() != null && !entity.getTargets().isEmpty())
      InstallCleaner.cleanTargets(entity.getTargets()
        .stream()
        .map(it -> ReinstallTarget.createFrom(it.getDatasetId(), it.getInstallTarget()))
        .toList());

    if (skipRun != true)
      DatasetReinstaller.tryRun();

    return PostAdminRpcInstallsClearFailedResponse.respond204();
  }

  @Override
  public PostAdminRpcObjectStorePurgeDatasetResponse postAdminRpcObjectStorePurgeDataset(DatasetObjectPurgeRequestBody entity) {
    if (entity == null || entity.getUserId() == null || entity.getDatasetId() == null)
      throw new BadRequestException();

    try {
      for (var obj : DatasetStore.listObjectsForDataset(entity.getUserId(), entity.getDatasetId()))
        obj.delete();
    } catch (Throwable e) {
      getLogger().error("failed to delete an object from the object store", e);
      throw new InternalServerErrorException();
    }

    return PostAdminRpcObjectStorePurgeDatasetResponse.respond204();
  }
}
