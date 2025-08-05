package vdi.service.rest.generated.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.ConflictError;
import vdi.service.rest.generated.model.DatasetObjectPurgeRequestBody;
import vdi.service.rest.generated.model.DatasetPostResponseBody;
import vdi.service.rest.generated.model.DatasetProxyPostRequestBody;
import vdi.service.rest.generated.model.ForbiddenError;
import vdi.service.rest.generated.model.InstallCleanupRequestBody;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnprocessableEntityError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/admin/rpc")
public interface AdminRpc {
  String ROOT_PATH = "/admin/rpc";

  String DATASETS_PRUNE_PATH = ROOT_PATH + "/datasets/prune";

  String OBJECT_STORE_PURGE_DATASET_PATH = ROOT_PATH + "/object-store/purge-dataset";

  String DATASETS_PROXY_UPLOAD_PATH = ROOT_PATH + "/datasets/proxy-upload";

  String DATASETS_RECONCILE_PATH = ROOT_PATH + "/datasets/reconcile";

  String INSTALLS_CLEAR_FAILED_PATH = ROOT_PATH + "/installs/clear-failed";

  @POST
  @Path("/datasets/proxy-upload")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostAdminRpcDatasetsProxyUploadResponse postAdminRpcDatasetsProxyUpload(
      @HeaderParam("User-ID") long userID, DatasetProxyPostRequestBody entity);

  @POST
  @Path("/datasets/prune")
  @Produces("application/json")
  PostAdminRpcDatasetsPruneResponse postAdminRpcDatasetsPrune(
      @QueryParam("age_cutoff") String ageCutoff);

  @POST
  @Path("/datasets/reconcile")
  @Produces("application/json")
  PostAdminRpcDatasetsReconcileResponse postAdminRpcDatasetsReconcile();

  @POST
  @Path("/installs/clear-failed")
  @Produces("application/json")
  @Consumes("application/json")
  PostAdminRpcInstallsClearFailedResponse postAdminRpcInstallsClearFailed(
      @QueryParam("skip-run") @DefaultValue("false") boolean skipRun,
      InstallCleanupRequestBody entity);

  @POST
  @Path("/object-store/purge-dataset")
  @Produces("application/json")
  @Consumes("application/json")
  PostAdminRpcObjectStorePurgeDatasetResponse postAdminRpcObjectStorePurgeDataset(
      DatasetObjectPurgeRequestBody entity);

  class PostAdminRpcDatasetsProxyUploadResponse extends ResponseDelegate {
    private PostAdminRpcDatasetsProxyUploadResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminRpcDatasetsProxyUploadResponse(Response response) {
      super(response);
    }

    public static PostAdminRpcDatasetsProxyUploadResponse respond200WithApplicationJson(
        DatasetPostResponseBody entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsProxyUploadResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsProxyUploadResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsProxyUploadResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsProxyUploadResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsProxyUploadResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminRpcDatasetsPruneResponse extends ResponseDelegate {
    private PostAdminRpcDatasetsPruneResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminRpcDatasetsPruneResponse(Response response) {
      super(response);
    }

    public static PostAdminRpcDatasetsPruneResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminRpcDatasetsPruneResponse(responseBuilder.build());
    }

    public static PostAdminRpcDatasetsPruneResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsPruneResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsPruneResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsPruneResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminRpcDatasetsReconcileResponse extends ResponseDelegate {
    private PostAdminRpcDatasetsReconcileResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminRpcDatasetsReconcileResponse(Response response) {
      super(response);
    }

    public static PostAdminRpcDatasetsReconcileResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminRpcDatasetsReconcileResponse(responseBuilder.build());
    }

    public static PostAdminRpcDatasetsReconcileResponse respond409WithApplicationJson(
        ConflictError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(409).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsReconcileResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcDatasetsReconcileResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcDatasetsReconcileResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminRpcInstallsClearFailedResponse extends ResponseDelegate {
    private PostAdminRpcInstallsClearFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminRpcInstallsClearFailedResponse(Response response) {
      super(response);
    }

    public static PostAdminRpcInstallsClearFailedResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminRpcInstallsClearFailedResponse(responseBuilder.build());
    }

    public static PostAdminRpcInstallsClearFailedResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcInstallsClearFailedResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcInstallsClearFailedResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcInstallsClearFailedResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminRpcObjectStorePurgeDatasetResponse extends ResponseDelegate {
    private PostAdminRpcObjectStorePurgeDatasetResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminRpcObjectStorePurgeDatasetResponse(Response response) {
      super(response);
    }

    public static PostAdminRpcObjectStorePurgeDatasetResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminRpcObjectStorePurgeDatasetResponse(responseBuilder.build());
    }

    public static PostAdminRpcObjectStorePurgeDatasetResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcObjectStorePurgeDatasetResponse(responseBuilder.build(), entity);
    }

    public static PostAdminRpcObjectStorePurgeDatasetResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminRpcObjectStorePurgeDatasetResponse(responseBuilder.build(), entity);
    }
  }
}
