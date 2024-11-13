package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.AllDatasetsListResponse;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.BrokenDatasetListing;
import org.veupathdb.service.vdi.generated.model.BrokenImportListing;
import org.veupathdb.service.vdi.generated.model.DatasetPostRequest;
import org.veupathdb.service.vdi.generated.model.DatasetPostResponse;
import org.veupathdb.service.vdi.generated.model.ForbiddenError;
import org.veupathdb.service.vdi.generated.model.InstallCleanupRequest;
import org.veupathdb.service.vdi.generated.model.InternalDatasetDetails;
import org.veupathdb.service.vdi.generated.model.NotFoundError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/admin")
public interface Admin {
  @POST
  @Path("/reconciler")
  PostAdminReconcilerResponse postAdminReconciler();

  @POST
  @Path("/proxy-upload")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostAdminProxyUploadResponse postAdminProxyUpload(@HeaderParam("User-ID") Long userID,
      DatasetPostRequest entity);

  @GET
  @Path("/failed-imports")
  @Produces("application/json")
  GetAdminFailedImportsResponse getAdminFailedImports(@QueryParam("user") Long user,
      @QueryParam("before") String before, @QueryParam("after") String after,
      @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("offset") @DefaultValue("0") Integer offset,
      @QueryParam("sort") @DefaultValue("date") String sort,
      @QueryParam("order") @DefaultValue("desc") String order);

  @GET
  @Path("/list-broken")
  @Produces("application/json")
  GetAdminListBrokenResponse getAdminListBroken(
      @QueryParam("expanded") @DefaultValue("true") Boolean expanded);

  @POST
  @Path("/fix-broken-installs")
  @Produces("application/json")
  @Consumes("application/json")
  PostAdminFixBrokenInstallsResponse postAdminFixBrokenInstalls(
      @QueryParam("skip-run") @DefaultValue("false") Boolean skipRun, InstallCleanupRequest entity);

  @POST
  @Path("/delete-cleanup")
  @Produces("application/json")
  PostAdminDeleteCleanupResponse postAdminDeleteCleanup();

  @GET
  @Path("/dataset-details")
  @Produces("application/json")
  GetAdminDatasetDetailsResponse getAdminDatasetDetails(@QueryParam("datasetId") String datasetId);

  @GET
  @Path("/list-s3-objects")
  @Produces("text/plain")
  GetAdminListS3ObjectsResponse getAdminListS3Objects();

  @GET
  @Path("/list-all-datasets")
  @Produces("application/json")
  GetAdminListAllDatasetsResponse getAdminListAllDatasets(
      @QueryParam("offset") @DefaultValue("0") Integer offset,
      @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("project_id") String projectId,
      @QueryParam("include_deleted") @DefaultValue("false") Boolean includeDeleted);

  @POST
  @Path("/purge-dataset")
  @Produces("application/json")
  PostAdminPurgeDatasetResponse postAdminPurgeDataset(@QueryParam("user-id") Long userId,
      @QueryParam("dataset-id") String datasetId);

  class PostAdminReconcilerResponse extends ResponseDelegate {
    private PostAdminReconcilerResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminReconcilerResponse(Response response) {
      super(response);
    }

    public static PostAdminReconcilerResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminReconcilerResponse(responseBuilder.build());
    }

    public static PostAdminReconcilerResponse respond409() {
      Response.ResponseBuilder responseBuilder = Response.status(409);
      return new PostAdminReconcilerResponse(responseBuilder.build());
    }
  }

  class PostAdminProxyUploadResponse extends ResponseDelegate {
    private PostAdminProxyUploadResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminProxyUploadResponse(Response response) {
      super(response);
    }

    public static PostAdminProxyUploadResponse respond200WithApplicationJson(
        DatasetPostResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminProxyUploadResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminProxyUploadResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminProxyUploadResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminProxyUploadResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostAdminProxyUploadResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminProxyUploadResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminFailedImportsResponse extends ResponseDelegate {
    private GetAdminFailedImportsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminFailedImportsResponse(Response response) {
      super(response);
    }

    public static GetAdminFailedImportsResponse respond200WithApplicationJson(
        BrokenImportListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminFailedImportsResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminListBrokenResponse extends ResponseDelegate {
    private GetAdminListBrokenResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminListBrokenResponse(Response response) {
      super(response);
    }

    public static GetAdminListBrokenResponse respond200WithApplicationJson(
        BrokenDatasetListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetAdminListBrokenResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetAdminListBrokenResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminListBrokenResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminFixBrokenInstallsResponse extends ResponseDelegate {
    private PostAdminFixBrokenInstallsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminFixBrokenInstallsResponse(Response response) {
      super(response);
    }

    public static PostAdminFixBrokenInstallsResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminFixBrokenInstallsResponse(responseBuilder.build());
    }

    public static PostAdminFixBrokenInstallsResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminFixBrokenInstallsResponse(responseBuilder.build(), entity);
    }

    public static PostAdminFixBrokenInstallsResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminFixBrokenInstallsResponse(responseBuilder.build(), entity);
    }

    public static PostAdminFixBrokenInstallsResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminFixBrokenInstallsResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminDeleteCleanupResponse extends ResponseDelegate {
    private PostAdminDeleteCleanupResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminDeleteCleanupResponse(Response response) {
      super(response);
    }

    public static PostAdminDeleteCleanupResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminDeleteCleanupResponse(responseBuilder.build());
    }

    public static PostAdminDeleteCleanupResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostAdminDeleteCleanupResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostAdminDeleteCleanupResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminDatasetDetailsResponse extends ResponseDelegate {
    private GetAdminDatasetDetailsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminDatasetDetailsResponse(Response response) {
      super(response);
    }

    public static GetAdminDatasetDetailsResponse respond200WithApplicationJson(
        InternalDatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminDatasetDetailsResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminListS3ObjectsResponse extends ResponseDelegate {
    private GetAdminListS3ObjectsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminListS3ObjectsResponse(Response response) {
      super(response);
    }

    public static GetAdminListS3ObjectsResponse respond200WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetAdminListS3ObjectsResponse(responseBuilder.build(), entity);
    }
  }

  class GetAdminListAllDatasetsResponse extends ResponseDelegate {
    private GetAdminListAllDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAdminListAllDatasetsResponse(Response response) {
      super(response);
    }

    public static GetAdminListAllDatasetsResponse respond200WithApplicationJson(
        AllDatasetsListResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAdminListAllDatasetsResponse(responseBuilder.build(), entity);
    }
  }

  class PostAdminPurgeDatasetResponse extends ResponseDelegate {
    private PostAdminPurgeDatasetResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAdminPurgeDatasetResponse(Response response) {
      super(response);
    }

    public static PostAdminPurgeDatasetResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostAdminPurgeDatasetResponse(responseBuilder.build());
    }

    public static PostAdminPurgeDatasetResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminPurgeDatasetResponse(responseBuilder.build(), entity);
    }

    public static PostAdminPurgeDatasetResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminPurgeDatasetResponse(responseBuilder.build(), entity);
    }

    public static PostAdminPurgeDatasetResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminPurgeDatasetResponse(responseBuilder.build(), entity);
    }

    public static PostAdminPurgeDatasetResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostAdminPurgeDatasetResponse(responseBuilder.build(), entity);
    }
  }
}
