package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
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
      @HeaderParam("User-ID") Long userID, DatasetProxyPostRequestBody entity);

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
      @QueryParam("skip-run") @DefaultValue("false") Boolean skipRun,
      InstallCleanupRequestBody entity);

  @POST
  @Path("/object-store/purge-dataset")
  @Produces("application/json")
  @Consumes("application/json")
  PostAdminRpcObjectStorePurgeDatasetResponse postAdminRpcObjectStorePurgeDataset(
      DatasetObjectPurgeRequestBody entity);

  class PostAdminRpcDatasetsProxyUploadResponse extends ResponseDelegate {
    public PostAdminRpcDatasetsProxyUploadResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostAdminRpcDatasetsProxyUploadResponse(Response response) {
      super(response);
    }

    public PostAdminRpcDatasetsProxyUploadResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostAdminRpcDatasetsPruneResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostAdminRpcDatasetsPruneResponse(Response response) {
      super(response);
    }

    public PostAdminRpcDatasetsPruneResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostAdminRpcDatasetsReconcileResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostAdminRpcDatasetsReconcileResponse(Response response) {
      super(response);
    }

    public PostAdminRpcDatasetsReconcileResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostAdminRpcInstallsClearFailedResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostAdminRpcInstallsClearFailedResponse(Response response) {
      super(response);
    }

    public PostAdminRpcInstallsClearFailedResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public PostAdminRpcObjectStorePurgeDatasetResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PostAdminRpcObjectStorePurgeDatasetResponse(Response response) {
      super(response);
    }

    public PostAdminRpcObjectStorePurgeDatasetResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
