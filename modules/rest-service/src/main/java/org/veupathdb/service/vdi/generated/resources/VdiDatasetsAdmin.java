package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.*;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-datasets/admin")
public interface VdiDatasetsAdmin {
  @POST
  @Path("/proxy-upload")
  @Produces("application/json")
  @Consumes("multipart/form-data")
  PostVdiDatasetsAdminProxyUploadResponse postVdiDatasetsAdminProxyUpload(
      @HeaderParam("User-ID") Long userID, DatasetPostRequest entity);

  @GET
  @Path("/failed-imports")
  @Produces("application/json")
  GetVdiDatasetsAdminFailedImportsResponse getVdiDatasetsAdminFailedImports(
      @QueryParam("user") Long user, @QueryParam("before") String before,
      @QueryParam("after") String after, @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("offset") @DefaultValue("0") Integer offset,
      @QueryParam("sort") @DefaultValue("date") String sort,
      @QueryParam("order") @DefaultValue("desc") String order);

  @GET
  @Path("/list-broken")
  @Produces("application/json")
  GetVdiDatasetsAdminListBrokenResponse getVdiDatasetsAdminListBroken(
      @QueryParam("expanded") @DefaultValue("true") Boolean expanded);

  @POST
  @Path("/install-cleanup")
  @Produces("application/json")
  @Consumes("application/json")
  PostVdiDatasetsAdminInstallCleanupResponse postVdiDatasetsAdminInstallCleanup(
      InstallCleanupRequest entity);

  @POST
  @Path("/delete-cleanup")
  @Produces("application/json")
  PostVdiDatasetsAdminDeleteCleanupResponse postVdiDatasetsAdminDeleteCleanup();

  @GET
  @Path("/dataset-details")
  @Produces("application/json")
  GetVdiDatasetsAdminDatasetDetailsResponse getVdiDatasetsAdminDatasetDetails(
      @QueryParam("datasetId") String datasetId);

  @GET
  @Path("/list-all-datasets")
  @Produces("application/json")
  GetVdiDatasetsAdminListAllDatasetsResponse getVdiDatasetsAdminListAllDatasets(
      @QueryParam("offset") @DefaultValue("0") Integer offset,
      @QueryParam("limit") @DefaultValue("100") Integer limit,
      @QueryParam("projectId") String projectId);

  class PostVdiDatasetsAdminProxyUploadResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminProxyUploadResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminProxyUploadResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond200WithApplicationJson(
        DatasetPostResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminProxyUploadResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminProxyUploadResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsAdminFailedImportsResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminFailedImportsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminFailedImportsResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminFailedImportsResponse respond200WithApplicationJson(
        BrokenImportListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminFailedImportsResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsAdminListBrokenResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminListBrokenResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminListBrokenResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond200WithApplicationJson(
        BrokenDatasetListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsAdminListBrokenResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListBrokenResponse(responseBuilder.build(), entity);
    }
  }

  class PostVdiDatasetsAdminInstallCleanupResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminInstallCleanupResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminInstallCleanupResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build());
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminInstallCleanupResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminInstallCleanupResponse(responseBuilder.build(), entity);
    }
  }

  class PostVdiDatasetsAdminDeleteCleanupResponse extends ResponseDelegate {
    private PostVdiDatasetsAdminDeleteCleanupResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostVdiDatasetsAdminDeleteCleanupResponse(Response response) {
      super(response);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build());
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }

    public static PostVdiDatasetsAdminDeleteCleanupResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostVdiDatasetsAdminDeleteCleanupResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsAdminDatasetDetailsResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminDatasetDetailsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminDatasetDetailsResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminDatasetDetailsResponse respond200WithApplicationJson(
        InternalDatasetDetails entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminDatasetDetailsResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsAdminListAllDatasetsResponse extends ResponseDelegate {
    private GetVdiDatasetsAdminListAllDatasetsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsAdminListAllDatasetsResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsAdminListAllDatasetsResponse respond200WithApplicationJson(
        AllDatasetsListResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsAdminListAllDatasetsResponse(responseBuilder.build(), entity);
    }
  }
}
